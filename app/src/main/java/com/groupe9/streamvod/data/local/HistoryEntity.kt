package com.groupe9.streamvod.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey
    val itemId: String,
    val title: String,
    val imageUrl: String,
    val type: String,
    val watchedAt: Long = System.currentTimeMillis()
)