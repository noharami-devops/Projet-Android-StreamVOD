package com.groupe9.streamvod.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY watchedAt DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToHistory(entry: HistoryEntity)

    @Query("DELETE FROM history WHERE itemId = :itemId")
    suspend fun removeFromHistory(itemId: String)

    @Query("DELETE FROM history")
    suspend fun clearHistory()
}