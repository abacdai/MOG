package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey
    val id: Int = 1, // Single row table
    val moonCoins: Int = 0,
    val streak: Int = 0,
    val totalFocusMinutesToday: Int = 0,
    val totalSessionsCompleted: Int = 0,
    val sleepHours: Float = 0f,
    val screenTimeHours: Float = 0f,
    val level: Int = 1,
    val lastFocusDate: String = "" // "YYYY-MM-DD"
)
