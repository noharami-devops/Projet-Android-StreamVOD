package com.groupe9.streamvod.data.repository

import com.groupe9.streamvod.data.local.HistoryDao
import com.groupe9.streamvod.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {
    fun getAllHistory(): Flow<List<HistoryEntity>> {
        return historyDao.getAllHistory()
    }

    suspend fun addToHistory(itemId: String, title: String, imageUrl: String, type: String) {
        historyDao.addToHistory(
            HistoryEntity(
                itemId = itemId,
                title = title,
                imageUrl = imageUrl,
                type = type,
                watchedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFromHistory(itemId: String) {
        historyDao.removeFromHistory(itemId)
    }

    suspend fun clearHistory() {
        historyDao.clearHistory()
    }
}