package com.example.game

import com.example.data.model.Difficulty
import kotlin.math.abs
import kotlin.random.Random

object PuzzleEngine {

    /**
     * Creates a solved board for the given difficulty:
     * [1, 2, 3, ..., N*N - 1, 0]
     */
    fun createSolvedBoard(difficulty: Difficulty): List<Int> {
        val total = difficulty.totalTiles
        return (1 until total).toList() + listOf(0)
    }

    /**
     * Checks if the board is in the solved state.
     */
    fun isSolved(board: List<Int>): Boolean {
        if (board.isEmpty()) return false
        val total = board.size
        for (i in 0 until total - 1) {
            if (board[i] != i + 1) return false
        }
        return board[total - 1] == 0
    }

    /**
     * Checks if a specific tile is currently in its correct target position.
     */
    fun isTileInCorrectPosition(board: List<Int>, index: Int): Boolean {
        val value = board.getOrNull(index) ?: return false
        if (value == 0) return index == board.size - 1
        return value == index + 1
    }

    /**
     * Shuffles the board starting from the solved state using a guaranteed-solvable random walk.
     */
    fun generateSolvableBoard(difficulty: Difficulty): List<Int> {
        val steps = when (difficulty) {
            Difficulty.EASY -> 60
            Difficulty.MEDIUM -> 140
            Difficulty.HARD -> 260
        }
        return generateBoardWithSteps(difficulty, steps)
    }

    /**
     * Generates a solvable board shuffled by specific step count for custom level progression.
     */
    fun generateBoardForLevel(difficulty: Difficulty, levelSteps: Int): List<Int> {
        return generateBoardWithSteps(difficulty, levelSteps)
    }

    private fun generateBoardWithSteps(difficulty: Difficulty, steps: Int): List<Int> {
        val n = difficulty.gridSize
        val board = createSolvedBoard(difficulty).toMutableList()

        var emptyIndex = board.indexOf(0)
        var lastMovedIndex = -1

        for (step in 0 until steps) {
            val emptyRow = emptyIndex / n
            val emptyCol = emptyIndex % n

            // Valid neighbor indices
            val neighbors = mutableListOf<Int>()
            if (emptyRow > 0) neighbors.add((emptyRow - 1) * n + emptyCol) // Top
            if (emptyRow < n - 1) neighbors.add((emptyRow + 1) * n + emptyCol) // Bottom
            if (emptyCol > 0) neighbors.add(emptyRow * n + (emptyCol - 1)) // Left
            if (emptyCol < n - 1) neighbors.add(emptyRow * n + (emptyCol + 1)) // Right

            // Prefer not to immediately undo the previous move
            val validChoices = if (neighbors.size > 1) {
                neighbors.filter { it != lastMovedIndex }
            } else {
                neighbors
            }

            val chosen = validChoices[Random.nextInt(validChoices.size)]
            board[emptyIndex] = board[chosen]
            board[chosen] = 0
            lastMovedIndex = emptyIndex
            emptyIndex = chosen
        }

        // If by rare chance it ended up solved, move once more
        if (isSolved(board)) {
            val emptyRow = emptyIndex / n
            val emptyCol = emptyIndex % n
            val swapWith = if (emptyRow > 0) (emptyRow - 1) * n + emptyCol else (emptyRow + 1) * n + emptyCol
            board[emptyIndex] = board[swapWith]
            board[swapWith] = 0
        }

        return board
    }

    /**
     * Attempts to slide the tapped tile (or line of tiles) toward the empty spot.
     * Returns the new board list if successful, or null if move is not possible.
     */
    fun tryMove(board: List<Int>, tappedIndex: Int, gridSize: Int): List<Int>? {
        val emptyIndex = board.indexOf(0)
        if (emptyIndex == -1 || tappedIndex == emptyIndex) return null

        val tappedRow = tappedIndex / gridSize
        val tappedCol = tappedIndex % gridSize
        val emptyRow = emptyIndex / gridSize
        val emptyCol = emptyIndex % gridSize

        // Can move if in the same row or same column
        if (tappedRow != emptyRow && tappedCol != emptyCol) {
            return null
        }

        val newBoard = board.toMutableList()

        if (tappedRow == emptyRow) {
            // Horizontal shift
            val step = if (tappedCol < emptyCol) 1 else -1
            var col = emptyCol
            while (col != tappedCol) {
                val nextCol = col - step
                val currentPos = tappedRow * gridSize + col
                val nextPos = tappedRow * gridSize + nextCol
                newBoard[currentPos] = newBoard[nextPos]
                col = nextCol
            }
            newBoard[tappedRow * gridSize + tappedCol] = 0
        } else {
            // Vertical shift
            val step = if (tappedRow < emptyRow) 1 else -1
            var row = emptyRow
            while (row != tappedRow) {
                val nextRow = row - step
                val currentPos = row * gridSize + tappedCol
                val nextPos = nextRow * gridSize + tappedCol
                newBoard[currentPos] = newBoard[nextPos]
                row = nextRow
            }
            newBoard[tappedRow * gridSize + tappedCol] = 0
        }

        return newBoard
    }

    /**
     * Suggests a helpful tile index to move next.
     */
    fun findHintMove(board: List<Int>, gridSize: Int): Int? {
        val emptyIndex = board.indexOf(0)
        if (emptyIndex == -1) return null

        val emptyRow = emptyIndex / gridSize
        val emptyCol = emptyIndex % gridSize

        val adjacent = mutableListOf<Int>()
        if (emptyRow > 0) adjacent.add((emptyRow - 1) * gridSize + emptyCol)
        if (emptyRow < gridSize - 1) adjacent.add((emptyRow + 1) * gridSize + emptyCol)
        if (emptyCol > 0) adjacent.add(emptyRow * gridSize + (emptyCol - 1))
        if (emptyCol < gridSize - 1) adjacent.add(emptyRow * gridSize + (emptyCol + 1))

        // Prefer adjacent tile that is NOT yet in correct position or that moves towards target
        return adjacent.firstOrNull { !isTileInCorrectPosition(board, it) } ?: adjacent.firstOrNull()
    }
}
