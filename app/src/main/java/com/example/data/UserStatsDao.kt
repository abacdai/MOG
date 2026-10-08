package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getStats(): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStats(stats: UserStats)

    @Query("UPDATE user_stats SET moonCoins = moonCoins + :amount WHERE id = 1")
    suspend fun addCoins(amount: Int)

    @Query("UPDATE user_stats SET moonCoins = moonCoins - :amount WHERE id = 1 AND moonCoins >= :amount")
    suspend fun spendCoins(amount: Int): Int

    @Query("UPDATE user_stats SET totalFocusMinutesToday = totalFocusMinutesToday + :minutes, totalSessionsCompleted = totalSessionsCompleted + 1 WHERE id = 1")
    suspend fun addFocusMinutes(minutes: Int)

    @Query("UPDATE user_stats SET totalFocusMinutesToday = :minutes WHERE id = 1")
    suspend fun resetDailyMinutes(minutes: Int = 0)
}
