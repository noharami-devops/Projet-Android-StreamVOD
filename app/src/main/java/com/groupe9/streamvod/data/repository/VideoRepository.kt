package com.groupe9.streamvod.data.repository

import com.groupe9.streamvod.BuildConfig
import com.groupe9.streamvod.data.remote.TmdbApiService
import com.groupe9.streamvod.data.remote.toVideo
import com.groupe9.streamvod.domain.model.Video
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val apiService: TmdbApiService
) {
    suspend fun getPopularMovies(): Result<List<Video>> {
        return try {
            val response = apiService.getPopularMovies(
                apiKey = "db5ab02bd48fcf4faa542c554adcdcb2"
            )
            Result.success(response.results.map { it.toVideo() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopRatedMovies(): Result<List<Video>> {
        return try {
            val response = apiService.getTopRatedMovies(
                apiKey = "db5ab02bd48fcf4faa542c554adcdcb2"
            )
            Result.success(response.results.map { it.toVideo() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchMovies(query: String): Result<List<Video>> {
        return try {
            val response = apiService.searchMovies(
                apiKey ="db5ab02bd48fcf4faa542c554adcdcb2",
                query = query
            )
            Result.success(response.results.map { it.toVideo() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getMovieDetail(movieId: Int): Result<Video> {
        return try {
            val response = apiService.getMovieDetail(
                movieId = movieId,
                apiKey = "db5ab02bd48fcf4faa542c554adcdcb2"
            )
            Result.success(response.toVideo())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}