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
    // 15 progressive handcrafted levels per difficulty = 45 total levels
    val easyLevels: List<Level> = listOf(
        Level(
            levelNumber = 1,
            title = "First Steps",
            subtitle = "Tutorial 1: Slide the glowing tile into the empty slot!",
            shuffleSteps = 2,
            parMoves = 4,
            parSeconds = 15,
            isTutorial = true,
            tutorialInstruction = "Tap the glowing tile adjacent to the empty spot to slide it into position. Arrange tiles 1 to 8!"
        ),
        Level(
            levelNumber = 2,
            title = "Gentle Glide",
            subtitle = "Tutorial 2: Strategy - Solve Row 1 first!",
            shuffleSteps = 6,
            parMoves = 8,
            parSeconds = 20,
            isTutorial = true,
            tutorialInstruction = "Strategy: Lock row 1 (tiles 1, 2, 3) in place first! Green indicator dots show when a tile is at its home spot."
        ),
        Level(
            levelNumber = 3,
            title = "Corner Tuck",
            subtitle = "Place tiles 1 and 2, then tuck in 3",
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
            shuffleSteps = 30,
            parMoves = 32,
            parSeconds = 40
        ),
        Level(
            levelNumber = 7,
            title = "Sprint Stage",
            subtitle = "Race against the clock",
            shuffleSteps = 38,
            parMoves = 38,
            parSeconds = 45
        ),
        Level(
            levelNumber = 8,
            title = "Precision Turn",
            subtitle = "Careful move management",
            shuffleSteps = 46,
            parMoves = 44,
            parSeconds = 50
        ),
        Level(
            levelNumber = 9,
            title = "Quick Silver",
            subtitle = "Sub-30 second challenge",
            shuffleSteps = 54,
            parMoves = 50,
            parSeconds = 55
        ),
        Level(
            levelNumber = 10,
            title = "Easy Apex",
            subtitle = "Grand 3x3 mid-point milestone",
            shuffleSteps = 62,
            parMoves = 55,
            parSeconds = 60
        ),
        Level(
            levelNumber = 11,
            title = "Serpentine Path",
            subtitle = "Snake the tiles through the grid",
            shuffleSteps = 68,
            parMoves = 60,
            parSeconds = 65
        ),
        Level(
            levelNumber = 12,
            title = "Zig-Zag Drift",
            subtitle = "Alternate diagonal moves",
            shuffleSteps = 74,
            parMoves = 65,
            parSeconds = 70
        ),
        Level(
            levelNumber = 13,
            title = "Cascade Fall",
            subtitle = "Chain reaction tile maneuvers",
            shuffleSteps = 80,
            parMoves = 70,
            parSeconds = 75
        ),
        Level(
            levelNumber = 14,
            title = "Diamond Swirl",
            subtitle = "Circular perimeter rotation",
            shuffleSteps = 86,
            parMoves = 75,
            parSeconds = 80
        ),
        Level(
            levelNumber = 15,
            title = "Grand Master 3x3",
            subtitle = "Ultimate master test of 8-puzzle",
            shuffleSteps = 95,
            parMoves = 80,
            parSeconds = 90
        )
    )

    val mediumLevels: List<Level> = (1..15).map { lvl ->
        val names = listOf(
            "Classic 15", "Quarter Turn", "Center Ring", "Four Corners",
            "Linear Flow", "Gridlock Breaker", "Rapid Transit", "Orbit Shift",
            "Matrix Maze", "Grand Master 15", "Neon Vortex", "Cyber Circuit",
            "Quantum Grid", "Stellar Shift", "Apex 4x4"
        )
        val shuffles = 18 + lvl * 10
        val parM = 30 + lvl * 7
        val parS = 50 + lvl * 12
        Level(
            levelNumber = lvl,
            title = names[lvl - 1],
            subtitle = "4x4 Medium Stage $lvl",
            shuffleSteps = shuffles,
            parMoves = parM,
            parSeconds = parS
        )
    }

    val hardLevels: List<Level> = (1..15).map { lvl ->
        val names = listOf(
            "24 Titan", "Deep Trench", "Grand Perimeter", "Labyrinth Walk",
            "Cascade Shift", "Hyper Spiral", "Quantum Flux", "Apex Predator",
            "Colosseum", "Immortal 24", "Obsidian Maze", "Celestial Sphere",
            "Infinity Loop", "Supernova", "Zenith 5x5"
        )
        val shuffles = 35 + lvl * 16
        val parM = 60 + lvl * 12
        val parS = 100 + lvl * 16
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
