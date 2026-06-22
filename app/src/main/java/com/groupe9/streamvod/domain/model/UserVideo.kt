package com.groupe9.streamvod.domain.model

data class UserVideo(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val uploaderEmail: String = "",
    val uploaderName: String = "",
    val uploaderId: String = "",
    val likes: Int = 0,
    val likedBy: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)