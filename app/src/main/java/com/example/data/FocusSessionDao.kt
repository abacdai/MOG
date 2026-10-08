package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSession): Long

    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getSessionsSince(sinceTimestamp: Long): Flow<List<FocusSession>>

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE timestamp >= :sinceTimestamp")
    fun getTotalMinutesSince(sinceTimestamp: Long): Flow<Int?>

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE timestamp >= :sinceTimestamp")
    suspend fun getTotalMinutesSinceSync(sinceTimestamp: Long): Int?
}
