package com.example.game

import java.util.PriorityQueue
import kotlin.math.abs

enum class SlideDirection(val arrow: String, val label: String) {
    UP("↑", "UP"),
    DOWN("↓", "DOWN"),
    LEFT("←", "LEFT"),
    RIGHT("→", "RIGHT")
}

enum class CoachTone {
    READY,
    EXCELLENT,
    TRYING,
    OFF_TRACK,
    SOLVED
}

data class CoachState(
    val progressFraction: Float = 0f,
    val correctTiles: Int = 0,
    val totalNumberedTiles: Int = 8,
    val manhattanDistance: Int = 0,
    val tone: CoachTone = CoachTone.READY,
    val message: String = "Coach: Tap or swipe a tile to start!",
    val offTrackStreak: Int = 0
)

data class GuidedHelpMove(
    val tileIndex: Int,
    val tileValue: Int,
    val direction: SlideDirection,
    val estimatedStepsRemaining: Int?,
    val instructionText: String,
    val strategyTip: String
)

/**
 * Real-time algorithmic puzzle coach & shortest-path solver.
 * Evaluates whether the player is making progress toward solving the numbers
 * and provides step-by-step directional guidance when the user clicks "Get Helped".
 */
object PuzzleCoach {

    /**
     * Computes total Manhattan distance of all numbered tiles (1..N*N-1) from their solved positions.
     */
    fun computeManhattanDistance(board: List<Int>, gridSize: Int): Int {
        if (board.isEmpty() || gridSize <= 0) return 0
        var total = 0
        for (i in board.indices) {
            val value = board[i]
            if (value == 0) continue
            val targetIndex = value - 1
            val currentRow = i / gridSize
            val currentCol = i % gridSize
            val targetRow = targetIndex / gridSize
            val targetCol = targetIndex % gridSize
            total += abs(currentRow - targetRow) + abs(currentCol - targetCol)
        }
        return total
    }

    /**
     * Counts how many numbered tiles are currently in their exact home slot.
     */
    fun countCorrectTiles(board: List<Int>): Int {
        var count = 0
        for (i in 0 until board.size - 1) {
            if (board[i] == i + 1) count++
        }
        return count
    }

    /**
     * Computes a smooth 0f..1f progress score representing how close the board is to solved,
     * using initial Manhattan distance as baseline so every helpful slide advances the horizontal line.
     */
    fun computeProgressFraction(
        board: List<Int>,
        gridSize: Int,
        baselineManhattan: Int
    ): Float {
        if (board.isEmpty()) return 0f
        if (PuzzleEngine.isSolved(board)) return 1f

        val totalNumbered = (board.size - 1).coerceAtLeast(1)
        val currentDist = computeManhattanDistance(board, gridSize)
        val effectiveBaseline = maxOf(baselineManhattan, currentDist, totalNumbered)

        val distanceScore = (1f - (currentDist.toFloat() / effectiveBaseline.toFloat())).coerceIn(0f, 1f)
        val correctRatio = countCorrectTiles(board).toFloat() / totalNumbered.toFloat()

        // Blend Manhattan closeness (70%) + exact locked tiles (30%) for responsive feedback
        return (distanceScore * 0.70f + correctRatio * 0.30f).coerceIn(0.04f, 0.99f)
    }

    /**
     * Creates the initial CoachState when a level starts or resumes.
     */
    fun createInitialState(board: List<Int>, gridSize: Int, baselineManhattan: Int): CoachState {
        val totalNumbered = (board.size - 1).coerceAtLeast(1)
        val correct = countCorrectTiles(board)
        val dist = computeManhattanDistance(board, gridSize)
        val progress = computeProgressFraction(board, gridSize, baselineManhattan)

        return if (PuzzleEngine.isSolved(board)) {
            CoachState(
                progressFraction = 1f,
                correctTiles = totalNumbered,
                totalNumberedTiles = totalNumbered,
                manhattanDistance = 0,
                tone = CoachTone.SOLVED,
                message = "Coach: Brilliant! All tiles solved!",
                offTrackStreak = 0
            )
        } else {
            CoachState(
                progressFraction = progress,
                correctTiles = correct,
                totalNumberedTiles = totalNumbered,
                manhattanDistance = dist,
                tone = CoachTone.READY,
                message = "Coach: $correct/$totalNumbered in place — slide toward home!",
                offTrackStreak = 0
            )
        }
    }

    /**
     * Evaluates a player's move and returns updated CoachState for the horizontal coach bar.
     */
    fun evaluateMove(
        previousBoard: List<Int>,
        newBoard: List<Int>,
        movedTileValue: Int,
        gridSize: Int,
        baselineManhattan: Int,
        previousOffTrackStreak: Int
    ): CoachState {
        val totalNumbered = (newBoard.size - 1).coerceAtLeast(1)
        val prevDist = computeManhattanDistance(previousBoard, gridSize)
        val newDist = computeManhattanDistance(newBoard, gridSize)
        val prevCorrect = countCorrectTiles(previousBoard)
        val newCorrect = countCorrectTiles(newBoard)
        val progress = computeProgressFraction(newBoard, gridSize, baselineManhattan)

        if (PuzzleEngine.isSolved(newBoard)) {
            return CoachState(
                progressFraction = 1f,
                correctTiles = totalNumbered,
                totalNumberedTiles = totalNumbered,
                manhattanDistance = 0,
                tone = CoachTone.SOLVED,
                message = "Coach: Perfect! Every number is solved!",
                offTrackStreak = 0
            )
        }

        val deltaDist = newDist - prevDist
        val deltaCorrect = newCorrect - prevCorrect

        return when {
            deltaCorrect > 0 -> {
                CoachState(
                    progressFraction = progress,
                    correctTiles = newCorrect,
                    totalNumberedTiles = totalNumbered,
                    manhattanDistance = newDist,
                    tone = CoachTone.EXCELLENT,
                    message = "Coach: Nice! Tile $movedTileValue locked into spot ($newCorrect/$totalNumbered)",
                    offTrackStreak = 0
                )
            }
            deltaDist < 0 -> {
                CoachState(
                    progressFraction = progress,
                    correctTiles = newCorrect,
                    totalNumberedTiles = totalNumbered,
                    manhattanDistance = newDist,
                    tone = CoachTone.EXCELLENT,
                    message = "Coach: Great move! Tile $movedTileValue is closer to home",
                    offTrackStreak = 0
                )
            }
            deltaDist == 0 || previousOffTrackStreak == 0 -> {
                val nextStreak = if (deltaDist > 0) previousOffTrackStreak + 1 else 0
                CoachState(
                    progressFraction = progress,
                    correctTiles = newCorrect,
                    totalNumberedTiles = totalNumbered,
                    manhattanDistance = newDist,
                    tone = CoachTone.TRYING,
                    message = "Coach: Good effort — route around the empty slot",
                    offTrackStreak = nextStreak
                )
            }
            else -> {
                val nextStreak = previousOffTrackStreak + 1
                CoachState(
                    progressFraction = progress,
                    correctTiles = newCorrect,
                    totalNumberedTiles = totalNumbered,
                    manhattanDistance = newDist,
                    tone = CoachTone.OFF_TRACK,
                    message = "Coach: Moving away from target — tap Get Helped!",
                    offTrackStreak = nextStreak
                )
            }
        }
    }

    /**
     * Finds the best next move and direction to guide the user when clicking "Get Helped" or Hint.
     * Uses exact A* search on 3x3 boards, and smart heuristic lookahead on 4x4/5x5/6x6 boards.
     */
    fun findGuidedHelp(
        board: List<Int>,
        gridSize: Int,
        lastEmptyIndex: Int? = null
    ): GuidedHelpMove? {
        if (board.isEmpty() || PuzzleEngine.isSolved(board)) return null
        val emptyIndex = board.indexOf(0)
        if (emptyIndex == -1) return null

        val strategyTip = computeStrategyTip(board, gridSize)

        // Exact A* solver for 3x3 boards
        if (gridSize == 3) {
            val path = solve3x3AStar(board, maxVisited = 6500)
            if (path != null && path.isNotEmpty()) {
                val nextTileIndex = path.first()
                val tileValue = board[nextTileIndex]
                val dir = computeDirection(nextTileIndex, emptyIndex, gridSize)
                return GuidedHelpMove(
                    tileIndex = nextTileIndex,
                    tileValue = tileValue,
                    direction = dir,
                    estimatedStepsRemaining = path.size,
                    instructionText = "Tap or swipe Tile $tileValue ${dir.arrow} (${dir.label}) into the empty slot",
                    strategyTip = strategyTip
                )
            }
        }

        // Fast 2-ply lookahead heuristic for 4x4, 5x5, 6x6 (prioritizes earliest unsolved tiles)
        val bestTileIndex = findBestLookaheadMove(board, gridSize, lastEmptyIndex) ?: return null
        val tileValue = board[bestTileIndex]
        val dir = computeDirection(bestTileIndex, emptyIndex, gridSize)

        return GuidedHelpMove(
            tileIndex = bestTileIndex,
            tileValue = tileValue,
            direction = dir,
            estimatedStepsRemaining = null,
            instructionText = "Tap or swipe Tile $tileValue ${dir.arrow} (${dir.label}) toward the empty slot",
            strategyTip = strategyTip
        )
    }

    private fun computeDirection(tileIndex: Int, emptyIndex: Int, gridSize: Int): SlideDirection {
        val tileRow = tileIndex / gridSize
        val tileCol = tileIndex % gridSize
        val emptyRow = emptyIndex / gridSize
        val emptyCol = emptyIndex % gridSize

        return when {
            tileRow < emptyRow -> SlideDirection.DOWN
            tileRow > emptyRow -> SlideDirection.UP
            tileCol < emptyCol -> SlideDirection.RIGHT
            else -> SlideDirection.LEFT
        }
    }

    private fun computeStrategyTip(board: List<Int>, gridSize: Int): String {
        for (row in 0 until gridSize - 1) {
            val rowSolved = (0 until gridSize).all { col ->
                val idx = row * gridSize + col
                board[idx] == idx + 1
            }
            if (!rowSolved) {
                val startNum = row * gridSize + 1
                val endNum = (row + 1) * gridSize
                return "Strategy: Focus on completing Row ${row + 1} (tiles $startNum to $endNum) first, then leave them locked."
            }
        }
        return "Strategy: Great! Only the bottom section remains — rotate tiles smoothly around the empty slot."
    }

    private data class AStarNode(
        val board: List<Int>,
        val gCost: Int,
        val fCost: Int,
        val firstMoveTileIndex: Int?,
        val parent: AStarNode?,
        val movedTileIndex: Int
    ) : Comparable<AStarNode> {
        override fun compareTo(other: AStarNode): Int = this.fCost.compareTo(other.fCost)
    }

    private fun solve3x3AStar(initialBoard: List<Int>, maxVisited: Int): List<Int>? {
        val gridSize = 3
        val startH = computeManhattanDistance(initialBoard, gridSize)
        val startNode = AStarNode(
            board = initialBoard,
            gCost = 0,
            fCost = startH,
            firstMoveTileIndex = null,
            parent = null,
            movedTileIndex = -1
        )

        val openSet = PriorityQueue<AStarNode>()
        val bestG = HashMap<List<Int>, Int>(1024)
        openSet.add(startNode)
        bestG[initialBoard] = 0

        var visited = 0
        while (openSet.isNotEmpty() && visited < maxVisited) {
            val current = openSet.poll() ?: break
            visited++

            if (PuzzleEngine.isSolved(current.board)) {
                val moves = mutableListOf<Int>()
                var cursor: AStarNode? = current
                while (cursor != null && cursor.movedTileIndex != -1) {
                    moves.add(cursor.movedTileIndex)
                    cursor = cursor.parent
                }
                moves.reverse()
                return moves
            }

            val emptyIdx = current.board.indexOf(0)
            val emptyRow = emptyIdx / gridSize
            val emptyCol = emptyIdx % gridSize

            val neighbors = ArrayList<Int>(4)
            if (emptyRow > 0) neighbors.add((emptyRow - 1) * gridSize + emptyCol)
            if (emptyRow < gridSize - 1) neighbors.add((emptyRow + 1) * gridSize + emptyCol)
            if (emptyCol > 0) neighbors.add(emptyRow * gridSize + (emptyCol - 1))
            if (emptyCol < gridSize - 1) neighbors.add(emptyRow * gridSize + (emptyCol + 1))

            for (neighborIdx in neighbors) {
                val nextBoard = current.board.toMutableList()
                nextBoard[emptyIdx] = nextBoard[neighborIdx]
                nextBoard[neighborIdx] = 0

                val nextG = current.gCost + 1
                val prevBest = bestG[nextBoard]
                if (prevBest == null || nextG < prevBest) {
                    bestG[nextBoard] = nextG
                    val h = computeManhattanDistance(nextBoard, gridSize)
                    openSet.add(
                        AStarNode(
                            board = nextBoard,
                            gCost = nextG,
                            fCost = nextG + h,
                            firstMoveTileIndex = current.firstMoveTileIndex ?: neighborIdx,
                            parent = current,
                            movedTileIndex = neighborIdx
                        )
                    )
                }
            }
        }
        return null
    }

    private fun findBestLookaheadMove(
        board: List<Int>,
        gridSize: Int,
        lastEmptyIndex: Int?
    ): Int? {
        val emptyIdx = board.indexOf(0)
        if (emptyIdx == -1) return null

        val neighbors = getAdjacentIndices(emptyIdx, gridSize)
        if (neighbors.isEmpty()) return null

        val candidates = if (neighbors.size > 1 && lastEmptyIndex != null) {
            neighbors.filter { it != lastEmptyIndex }.ifEmpty { neighbors }
        } else {
            neighbors
        }

        var bestCandidate = candidates.first()
        var bestScore = Int.MAX_VALUE

        for (candidateIdx in candidates) {
            val after1 = swapWithEmpty(board, candidateIdx, emptyIdx)
            if (PuzzleEngine.isSolved(after1)) return candidateIdx

            // 2-ply lookahead
            val nextNeighbors = getAdjacentIndices(candidateIdx, gridSize).filter { it != emptyIdx }
            var minSecondPlyScore = scoreBoardHeuristic(after1, gridSize)
            for (secondIdx in nextNeighbors) {
                val after2 = swapWithEmpty(after1, secondIdx, candidateIdx)
                val s2 = scoreBoardHeuristic(after2, gridSize)
                if (s2 < minSecondPlyScore) {
                    minSecondPlyScore = s2
                }
            }

            if (minSecondPlyScore < bestScore) {
                bestScore = minSecondPlyScore
                bestCandidate = candidateIdx
            }
        }

        return bestCandidate
    }

    private fun scoreBoardHeuristic(board: List<Int>, gridSize: Int): Int {
        var score = 0
        for (i in board.indices) {
            val value = board[i]
            if (value == 0) continue
            val targetIdx = value - 1
            val r1 = i / gridSize
            val c1 = i % gridSize
            val r2 = targetIdx / gridSize
            val c2 = targetIdx % gridSize
            // Weight earlier rows slightly higher to encourage row-by-row solving
            val rowWeight = (gridSize - r2).coerceAtLeast(1)
            score += (abs(r1 - r2) + abs(c1 - c2)) * (10 + rowWeight * 2)
            if (i != targetIdx) {
                score += 6
            }
        }
        return score
    }

    private fun getAdjacentIndices(emptyIdx: Int, gridSize: Int): List<Int> {
        val r = emptyIdx / gridSize
        val c = emptyIdx % gridSize
        val list = ArrayList<Int>(4)
        if (r > 0) list.add((r - 1) * gridSize + c)
        if (r < gridSize - 1) list.add((r + 1) * gridSize + c)
        if (c > 0) list.add(r * gridSize + (c - 1))
        if (c < gridSize - 1) list.add(r * gridSize + (c + 1))
        return list
    }

    private fun swapWithEmpty(board: List<Int>, tileIdx: Int, emptyIdx: Int): List<Int> {
        val next = board.toMutableList()
        next[emptyIdx] = next[tileIdx]
        next[tileIdx] = 0
        return next
    }
}
