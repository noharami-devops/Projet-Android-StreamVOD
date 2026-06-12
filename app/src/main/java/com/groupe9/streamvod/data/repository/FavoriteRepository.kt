package com.groupe9.streamvod.data.repository

import com.groupe9.streamvod.data.local.FavoriteDao
import com.groupe9.streamvod.data.local.FavoriteEntity
import com.groupe9.streamvod.domain.model.Video
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    fun getAllFavorites(): Flow<List<Video>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map { it.toVideo() }
        }
    }

    suspend fun addFavorite(video: Video) {
        favoriteDao.addFavorite(video.toEntity())
    }

    suspend fun removeFavorite(video: Video) {
        favoriteDao.removeFavorite(video.toEntity())
    }

    fun isFavorite(movieId: Int): Flow<Boolean> {
        return favoriteDao.isFavorite(movieId)
    }
}

// Extensions
fun FavoriteEntity.toVideo() = Video(
    id = id,
    title = title,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    releaseDate = releaseDate,
    description = description
)

fun Video.toEntity() = FavoriteEntity(
    id = id,
    title = title,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    releaseDate = releaseDate,
    description = description
)