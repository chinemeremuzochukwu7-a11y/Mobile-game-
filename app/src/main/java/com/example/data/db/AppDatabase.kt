package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [LeaderboardEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun leaderboardDao(): LeaderboardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "puzzle_master_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialLeaderboard(database.leaderboardDao())
                }
            }
        }

        private suspend fun populateInitialLeaderboard(dao: LeaderboardDao) {
            if (dao.getCount() > 0) return

            val seedScores = listOf(
                // Easy 3x3
                LeaderboardEntity(playerName = "Kenji_T", difficulty = "EASY", timeMillis = 18400L, moves = 22, score = 1420, countryFlag = "🇯🇵"),
                LeaderboardEntity(playerName = "Elena_R", difficulty = "EASY", timeMillis = 21200L, moves = 26, score = 1380, countryFlag = "🇪🇸"),
                LeaderboardEntity(playerName = "Marcus_99", difficulty = "EASY", timeMillis = 24800L, moves = 28, score = 1310, countryFlag = "🇩🇪"),
                LeaderboardEntity(playerName = "Sarah_K", difficulty = "EASY", timeMillis = 28100L, moves = 31, score = 1260, countryFlag = "🇺🇸"),
                LeaderboardEntity(playerName = "Lucas_Silva", difficulty = "EASY", timeMillis = 32500L, moves = 34, score = 1200, countryFlag = "🇧🇷"),
                LeaderboardEntity(playerName = "Chloe_M", difficulty = "EASY", timeMillis = 36000L, moves = 38, score = 1150, countryFlag = "🇫🇷"),
                LeaderboardEntity(playerName = "Min-Jun", difficulty = "EASY", timeMillis = 43100L, moves = 44, score = 1040, countryFlag = "🇰🇷"),

                // Medium 4x4
                LeaderboardEntity(playerName = "Alex_Blitz", difficulty = "MEDIUM", timeMillis = 54200L, moves = 56, score = 3320, countryFlag = "🇺🇸"),
                LeaderboardEntity(playerName = "Yuki_S", difficulty = "MEDIUM", timeMillis = 62000L, moves = 64, score = 3210, countryFlag = "🇯🇵"),
                LeaderboardEntity(playerName = "Matteo_B", difficulty = "MEDIUM", timeMillis = 71500L, moves = 69, score = 3080, countryFlag = "🇮🇹"),
                LeaderboardEntity(playerName = "Freja_Nordic", difficulty = "MEDIUM", timeMillis = 80300L, moves = 76, score = 2950, countryFlag = "🇸🇪"),
                LeaderboardEntity(playerName = "Chen_Wei", difficulty = "MEDIUM", timeMillis = 89600L, moves = 82, score = 2840, countryFlag = "🇹🇼"),
                LeaderboardEntity(playerName = "Liam_Puzzle", difficulty = "MEDIUM", timeMillis = 98400L, moves = 88, score = 2700, countryFlag = "🇬🇧"),
                LeaderboardEntity(playerName = "Camila_O", difficulty = "MEDIUM", timeMillis = 109100L, moves = 95, score = 2550, countryFlag = "🇲🇽"),

                // Hard 5x5
                LeaderboardEntity(playerName = "GrandMaster_X", difficulty = "HARD", timeMillis = 132400L, moves = 128, score = 6650, countryFlag = "🇨🇭"),
                LeaderboardEntity(playerName = "Daiki_Solve", difficulty = "HARD", timeMillis = 148900L, moves = 140, score = 6420, countryFlag = "🇯🇵"),
                LeaderboardEntity(playerName = "Viktor_Q", difficulty = "HARD", timeMillis = 164200L, moves = 152, score = 6200, countryFlag = "🇳🇴"),
                LeaderboardEntity(playerName = "Aaliyah_Stars", difficulty = "HARD", timeMillis = 181500L, moves = 165, score = 5980, countryFlag = "🇿🇦"),
                LeaderboardEntity(playerName = "Leo_Matrix", difficulty = "HARD", timeMillis = 199000L, moves = 178, score = 5740, countryFlag = "🇦🇺"),
                LeaderboardEntity(playerName = "Nadia_V", difficulty = "HARD", timeMillis = 218400L, moves = 192, score = 5510, countryFlag = "🇺🇦")
            )

            dao.insertAll(seedScores)
        }
    }
}
