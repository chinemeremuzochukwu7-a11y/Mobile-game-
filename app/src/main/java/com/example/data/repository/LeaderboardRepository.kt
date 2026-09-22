package com.example.data.repository

import com.example.data.db.LeaderboardDao
import com.example.data.db.LeaderboardEntity
import com.example.data.model.Difficulty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LeaderboardRepository(
    private val leaderboardDao: LeaderboardDao
) {
    fun getScoresByDifficulty(difficulty: Difficulty): Flow<List<LeaderboardEntity>> {
        return leaderboardDao.getScoresByDifficulty(difficulty.name)
    }

    fun getAllScores(): Flow<List<LeaderboardEntity>> {
        return leaderboardDao.getAllScores()
    }

    fun getUserScores(): Flow<List<LeaderboardEntity>> {
        return leaderboardDao.getUserScores()
    }

    fun getBestUserScore(difficulty: Difficulty): Flow<LeaderboardEntity?> {
        return leaderboardDao.getBestUserScore(difficulty.name)
    }

    suspend fun submitScore(
        playerName: String,
        difficulty: Difficulty,
        timeMillis: Long,
        moves: Int,
        countryFlag: String = "⭐"
    ): Long = withContext(Dispatchers.IO) {
        val totalSeconds = (timeMillis / 1000).toInt()
        val calculatedScore = difficulty.calculateScore(moves, totalSeconds)
        val entity = LeaderboardEntity(
            playerName = playerName.trim().ifEmpty { "Player" },
            difficulty = difficulty.name,
            timeMillis = timeMillis,
            moves = moves,
            score = calculatedScore,
            countryFlag = countryFlag,
            timestamp = System.currentTimeMillis(),
            isUser = true
        )
        leaderboardDao.insertScore(entity)
    }
}
