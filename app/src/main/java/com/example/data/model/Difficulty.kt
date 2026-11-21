package com.example.data.model

enum class Difficulty(
    val title: String,
    val subtitle: String,
    val gridSize: Int,
    val parMoves: Int,
    val parSeconds: Int,
    val baseScore: Int
) {
    EASY(
        title = "Easy",
        subtitle = "3x3 Grid (8 tiles)",
        gridSize = 3,
        parMoves = 35,
        parSeconds = 45,
        baseScore = 1500
    ),
    MEDIUM(
        title = "Medium",
        subtitle = "4x4 Grid (15 tiles)",
        gridSize = 4,
        parMoves = 80,
        parSeconds = 120,
        baseScore = 3500
    ),
    HARD(
        title = "Hard",
        subtitle = "5x5 Grid (24 tiles)",
        gridSize = 5,
        parMoves = 180,
        parSeconds = 240,
        baseScore = 7000
    ),
    EXPERT(
        title = "Expert",
        subtitle = "6x6 Grid (35 tiles)",
        gridSize = 6,
        parMoves = 320,
        parSeconds = 420,
        baseScore = 12000
    );

    val totalTiles: Int
        get() = gridSize * gridSize

    fun calculateScore(moves: Int, timeSeconds: Int): Int {
        val movePenalty = maxOf(0, moves - (parMoves / 2)) * 12
        val timePenalty = timeSeconds * 6
        val raw = baseScore - movePenalty - timePenalty
        return maxOf(100, raw)
    }

    fun calculateStars(moves: Int, timeSeconds: Int): Int {
        val score = calculateScore(moves, timeSeconds)
        val ratio = score.toFloat() / baseScore.toFloat()
        return when {
            ratio >= 0.75f -> 3
            ratio >= 0.45f -> 2
            else -> 1
        }
    }

    fun defaultTheme(): TileTheme = when (this) {
        EASY -> TileTheme.CLASSIC_WOOD
        MEDIUM -> TileTheme.MINIMALIST_NEON
        HARD -> TileTheme.NATURE
        EXPERT -> TileTheme.DARK_WALNUT
    }
}

enum class TileTheme(
    val displayName: String,
    val description: String,
    val associatedDifficulty: Difficulty
) {
    CLASSIC_WOOD(
        displayName = "Classic Wood",
        description = "Warm teak & mahogany craftsmanship for relaxed solving",
        associatedDifficulty = Difficulty.EASY
    ),
    DARK_WALNUT(
        displayName = "Dark Walnut",
        description = "Rich espresso walnut planks with warm brass highlights",
        associatedDifficulty = Difficulty.EXPERT
    ),
    MINIMALIST_NEON(
        displayName = "Minimalist Neon",
        description = "Cyberpunk high-contrast cyan, indigo, and ultraviolet",
        associatedDifficulty = Difficulty.MEDIUM
    ),
    NATURE(
        displayName = "Nature",
        description = "Organic emerald forest, moss gradients, and earthy jade",
        associatedDifficulty = Difficulty.HARD
    ),
    STEEL_INDUSTRIAL(
        displayName = "Brushed Steel",
        description = "Machined alloy plates with industrial corner rivets",
        associatedDifficulty = Difficulty.HARD
    )
}
