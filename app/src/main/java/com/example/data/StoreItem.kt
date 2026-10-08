package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_items")
data class StoreItem(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String, // "room", "furniture", "plant", "pet"
    val price: Int,
    val iconEmoji: String,
    val description: String = "",
    val isPurchased: Boolean = false,
    val isEquipped: Boolean = false
)
