package com.example.data.model

data class Level(
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val shuffleSteps: Int,
    val parMoves: Int,
    val parSeconds: Int,
    val isTutorial: Boolean = false,
    val tutorialInstruction: String = ""
)

object LevelCatalog {
    // 10 levels per difficulty = 30 handcrafted progressive levels
    val easyLevels: List<Level> = listOf(
        Level(
            levelNumber = 1,
            title = "First Steps",
            subtitle = "Tutorial: Slide the glowing tile into the empty slot!",
            shuffleSteps = 2,
            parMoves = 4,
            parSeconds = 15,
            isTutorial = true,
            tutorialInstruction = "Tap the glowing tile adjacent to the empty slot to slide it in place!"
        ),
        Level(
            levelNumber = 2,
            title = "Gentle Glide",
            subtitle = "Just a few moves to solve",
            shuffleSteps = 6,
            parMoves = 8,
            parSeconds = 20
        ),
        Level(
            levelNumber = 3,
            title = "Corner Tuck",
            subtitle = "Place tiles 1 and 2 first",
            shuffleSteps = 12,
            parMoves = 14,
            parSeconds = 25
        ),
        Level(
            levelNumber = 4,
            title = "Row Alignment",
            subtitle = "Complete row 1, then solve row 2",
            shuffleSteps = 18,
            parMoves = 20,
            parSeconds = 30
        ),
        Level(
            levelNumber = 5,
            title = "Speed Run",
            subtitle = "Test your quick reflexes",
            shuffleSteps = 24,
            parMoves = 26,
            parSeconds = 35
        ),
        Level(
            levelNumber = 6,
            title = "Pivot Shuffle",
            subtitle = "Rotate tiles around the center",
            shuffleSteps = 32,
            parMoves = 32,
            parSeconds = 40
        ),
        Level(
            levelNumber = 7,
            title = "Sprint Stage",
            subtitle = "Race against the clock",
            shuffleSteps = 40,
            parMoves = 38,
            parSeconds = 45
        ),
        Level(
            levelNumber = 8,
            title = "Precision Turn",
            subtitle = "Careful move management",
            shuffleSteps = 50,
            parMoves = 44,
            parSeconds = 50
        ),
        Level(
            levelNumber = 9,
            title = "Quick Silver",
            subtitle = "Sub-30 second challenge",
            shuffleSteps = 60,
            parMoves = 50,
            parSeconds = 55
        ),
        Level(
            levelNumber = 10,
            title = "Easy Apex",
            subtitle = "Grand finale of the 3x3 track",
            shuffleSteps = 70,
            parMoves = 55,
            parSeconds = 60
        )
    )

    val mediumLevels: List<Level> = (1..10).map { lvl ->
        val names = listOf(
            "Classic 15", "Quarter Turn", "Center Ring", "Four Corners",
            "Linear Flow", "Gridlock Breaker", "Rapid Transit", "Orbit Shift",
            "Matrix Maze", "Grand Master 15"
        )
        val shuffles = 20 + lvl * 14
        val parM = 35 + lvl * 9
        val parS = 60 + lvl * 15
        Level(
            levelNumber = lvl,
            title = names[lvl - 1],
            subtitle = "4x4 Medium Stage $lvl",
            shuffleSteps = shuffles,
            parMoves = parM,
            parSeconds = parS
        )
    }

    val hardLevels: List<Level> = (1..10).map { lvl ->
        val names = listOf(
            "24 Titan", "Deep Trench", "Grand Perimeter", "Labyrinth Walk",
            "Cascade Shift", "Hyper Spiral", "Quantum Flux", "Apex Predator",
            "Colosseum", "Immortal 24"
        )
        val shuffles = 40 + lvl * 22
        val parM = 70 + lvl * 16
        val parS = 120 + lvl * 20
        Level(
            levelNumber = lvl,
            title = names[lvl - 1],
            subtitle = "5x5 Hard Stage $lvl",
            shuffleSteps = shuffles,
            parMoves = parM,
            parSeconds = parS
        )
    }

    fun getLevelsForDifficulty(difficulty: Difficulty): List<Level> = when (difficulty) {
        Difficulty.EASY -> easyLevels
        Difficulty.MEDIUM -> mediumLevels
        Difficulty.HARD -> hardLevels
    }

    fun getLevel(difficulty: Difficulty, levelNumber: Int): Level {
        val list = getLevelsForDifficulty(difficulty)
        return list.firstOrNull { it.levelNumber == levelNumber } ?: list.first()
    }
}
