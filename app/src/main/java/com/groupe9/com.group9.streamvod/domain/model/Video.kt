package com.groupe9.streamvod.domain.model

data class Video(
    val id: Int,
    val title: String,
    val description: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val releaseDate: String,
    val isFavorite: Boolean = false,
    val videoUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
)