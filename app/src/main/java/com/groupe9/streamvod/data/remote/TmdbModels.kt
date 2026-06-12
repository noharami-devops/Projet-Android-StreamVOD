package com.groupe9.streamvod.data.remote

import com.google.gson.annotations.SerializedName

data class TmdbResponse(
    @SerializedName("results")
    val results: List<TmdbMovie>
)

data class TmdbMovie(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("release_date")
    val releaseDate: String
)

// Convertir TmdbMovie en Video
fun TmdbMovie.toVideo(): com.groupe9.streamvod.domain.model.Video {
    return com.groupe9.streamvod.domain.model.Video(
        id = id,
        title = title,
        description = overview,
        posterUrl = "https://image.tmdb.org/t/p/w500${posterPath}",
        backdropUrl = "https://image.tmdb.org/t/p/w780${backdropPath}",
        rating = voteAverage,
        releaseDate = releaseDate
    )
}