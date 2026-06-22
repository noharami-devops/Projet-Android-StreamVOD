package com.groupe9.streamvod.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.groupe9.streamvod.domain.model.Video
import com.groupe9.streamvod.ui.theme.*

@Composable
fun DetailScreen(
    movieId: Int,
    onBackClick: () -> Unit,
    onWatchClick: (Int) -> Unit,
    onRecommendationClick: (Int) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = Primary,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            uiState.video?.let { video ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Image backdrop
                    Box {
                        AsyncImage(
                            model = video.backdropUrl,
                            contentDescription = video.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentScale = ContentScale.Crop
                        )

                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Retour",
                                tint = OnBackground
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleFavorite(video) },
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = if (uiState.isFavorite)
                                    Icons.Default.Favorite
                                else
                                    Icons.Default.FavoriteBorder,
                                contentDescription = "Favoris",
                                tint = if (uiState.isFavorite) Primary else OnBackground
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = OnBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "⭐ ${String.format("%.1f", video.rating)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Accent
                            )
                            Text(
                                text = "📅 ${video.releaseDate}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Synopsis",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = video.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        val context = androidx.compose.ui.platform.LocalContext.current

                        Button(
                            onClick = { viewModel.openTrailer(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary
                            )
                        ) {
                            Text(
                                text = "▶ Voir la bande-annonce",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    // Section Recommandations
                    if (uiState.recommendations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🎯 Recommandé pour vous",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnBackground,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.recommendations) { recommendedVideo ->
                                RecommendedMovieCard(
                                    movie = recommendedVideo,
                                    onClick = { onRecommendationClick(recommendedVideo.id) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        uiState.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun RecommendedMovieCard(
    movie: Video,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = movie.title,
            modifier = Modifier
                .width(130.dp)
                .height(195.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.bodyMedium,
            color = OnBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "⭐ ${String.format("%.1f", movie.rating)}",
            style = MaterialTheme.typography.labelSmall,
            color = Accent
        )
    }
}