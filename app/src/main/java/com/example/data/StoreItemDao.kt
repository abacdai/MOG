package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreItemDao {
    @Query("SELECT * FROM store_items ORDER BY price ASC")
    fun getAllItems(): Flow<List<StoreItem>>

    @Query("SELECT * FROM store_items WHERE isPurchased = 1")
    fun getPurchasedItems(): Flow<List<StoreItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialItems(items: List<StoreItem>)

    @Query("UPDATE store_items SET isPurchased = 1 WHERE id = :itemId")
    suspend fun markAsPurchased(itemId: String)

    @Query("SELECT * FROM store_items WHERE id = :itemId")
    suspend fun getItemById(itemId: String): StoreItem?

    @Query("SELECT COUNT(*) FROM store_items")
    suspend fun getItemCount(): Int
}
