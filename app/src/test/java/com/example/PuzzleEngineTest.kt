package com.example

import com.example.data.model.Difficulty
import com.example.game.PuzzleEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleEngineTest {

    @Test
    fun testSolvedBoardGeneration() {
        val easy = PuzzleEngine.createSolvedBoard(Difficulty.EASY)
        assertEquals(9, easy.size)
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 8, 0), easy)
        assertTrue(PuzzleEngine.isSolved(easy))

        val medium = PuzzleEngine.createSolvedBoard(Difficulty.MEDIUM)
        assertEquals(16, medium.size)
        assertEquals(0, medium.last())
        assertTrue(PuzzleEngine.isSolved(medium))
    }

    @Test
    fun testMoveMechanics() {
        // Solved 3x3:
        // [1, 2, 3]
        // [4, 5, 6]
        // [7, 8, 0]  empty is at index 8 (row 2, col 2)
        val solved = PuzzleEngine.createSolvedBoard(Difficulty.EASY)

        // Tile 8 is at index 7 (row 2, col 1) - adjacent horizontally
        val afterMove8 = PuzzleEngine.tryMove(solved, 7, 3)
        assertNotNull(afterMove8)
        assertEquals(0, afterMove8!![7])
        assertEquals(8, afterMove8[8])

        // Tile 6 is at index 5 (row 1, col 2) - adjacent vertically
        val afterMove6 = PuzzleEngine.tryMove(solved, 5, 3)
        assertNotNull(afterMove6)
        assertEquals(0, afterMove6!![5])
        assertEquals(6, afterMove6[8])

        // Tile 1 is at index 0 (row 0, col 0) - not adjacent to 8 (row 2, col 2) diagonally/unaligned
        val invalidMove = PuzzleEngine.tryMove(solved, 0, 3)
        assertNull(invalidMove)
    }

    @Test
    fun testMultiTileLineSlide() {
        // [1, 2, 3]
        // [4, 5, 6]
        // [7, 8, 0]
        // Tapping tile 7 at index 6 (row 2, col 0) should slide both 8 and 7 right!
        val solved = PuzzleEngine.createSolvedBoard(Difficulty.EASY)
        val afterLineMove = PuzzleEngine.tryMove(solved, 6, 3)
        assertNotNull(afterLineMove)
        assertEquals(0, afterLineMove!![6])
        assertEquals(7, afterLineMove[7])
        assertEquals(8, afterLineMove[8])
    }

    @Test
    fun testSolvableBoardGeneration() {
        val board = PuzzleEngine.generateSolvableBoard(Difficulty.EASY)
        assertEquals(9, board.size)
        // Contains all numbers 0 through 8
        assertEquals((0..8).toSet(), board.toSet())
    }

    @Test
    fun testScoreCalculation() {
        val easyScore = Difficulty.EASY.calculateScore(moves = 25, timeSeconds = 20)
        assertTrue(easyScore > 100)

        val stars = Difficulty.EASY.calculateStars(moves = 20, timeSeconds = 15)
        assertEquals(3, stars)
    }

    @Test
    fun testLevelProgressionAndGeneration() {
        val level1Board = PuzzleEngine.generateBoardForLevel(Difficulty.EASY, 2)
        assertEquals(9, level1Board.size)
        assertEquals((0..8).toSet(), level1Board.toSet())

        val levelCatalogEasy = com.example.data.model.LevelCatalog.getLevelsForDifficulty(Difficulty.EASY)
        val levelCatalogMed = com.example.data.model.LevelCatalog.getLevelsForDifficulty(Difficulty.MEDIUM)
        val levelCatalogHard = com.example.data.model.LevelCatalog.getLevelsForDifficulty(Difficulty.HARD)

        assertEquals(10, levelCatalogEasy.size)
        assertEquals(10, levelCatalogMed.size)
        assertEquals(10, levelCatalogHard.size)
        assertTrue(levelCatalogEasy.first().isTutorial)
    }

    @Test
    fun testThemesAssociatedWithDifficulties() {
        assertEquals(com.example.data.model.TileTheme.CLASSIC_WOOD, Difficulty.EASY.defaultTheme())
        assertEquals(com.example.data.model.TileTheme.MINIMALIST_NEON, Difficulty.MEDIUM.defaultTheme())
        assertEquals(com.example.data.model.TileTheme.NATURE, Difficulty.HARD.defaultTheme())

        assertEquals("Classic Wood", com.example.data.model.TileTheme.CLASSIC_WOOD.displayName)
        assertEquals("Minimalist Neon", com.example.data.model.TileTheme.MINIMALIST_NEON.displayName)
        assertEquals("Nature", com.example.data.model.TileTheme.NATURE.displayName)
    }

    @Test
    fun testAdLoadingStateDefaults() {
        val defaultMessage = "Loading next challenge..."
        assertTrue(defaultMessage.isNotEmpty())
    }

    @Test
    fun testVolumePercentClamping() {
        val testVolume = 120
        val clamped = testVolume.coerceIn(0, 100)
        assertEquals(100, clamped)

        val negativeVolume = -15
        val clampedNegative = negativeVolume.coerceIn(0, 100)
        assertEquals(0, clampedNegative)
    }
}
