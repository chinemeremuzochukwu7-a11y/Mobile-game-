package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playerName: String,
    val difficulty: String,
    val timeMillis: Long,
    val moves: Int,
    val score: Int,
    val countryFlag: String = "⭐",
    val timestamp: Long = System.currentTimeMillis(),
    val isUser: Boolean = false
) {
    val formattedTime: String
        get() {
            val totalSeconds = timeMillis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val tenths = (timeMillis % 1000) / 100
            return String.format("%02d:%02d.%d", minutes, seconds, tenths)
        }
}
