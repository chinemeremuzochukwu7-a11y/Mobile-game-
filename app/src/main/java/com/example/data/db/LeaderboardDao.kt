package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard WHERE difficulty = :difficulty ORDER BY score DESC, timeMillis ASC, moves ASC LIMIT 50")
    fun getScoresByDifficulty(difficulty: String): Flow<List<LeaderboardEntity>>

    @Query("SELECT * FROM leaderboard ORDER BY score DESC, timeMillis ASC, moves ASC LIMIT 100")
    fun getAllScores(): Flow<List<LeaderboardEntity>>

    @Query("SELECT * FROM leaderboard WHERE isUser = 1 ORDER BY score DESC, timeMillis ASC, moves ASC")
    fun getUserScores(): Flow<List<LeaderboardEntity>>

    @Query("SELECT * FROM leaderboard WHERE isUser = 1 AND difficulty = :difficulty ORDER BY score DESC, timeMillis ASC, moves ASC LIMIT 1")
    fun getBestUserScore(difficulty: String): Flow<LeaderboardEntity?>

    @Query("SELECT COUNT(*) FROM leaderboard")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: LeaderboardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scores: List<LeaderboardEntity>)
}
