package com.groupe9.streamvod.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun HomeScreen(
    onMovieClick: (Int) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        item {
            Text(
                text = "🎬 StreamVOD",
                style = MaterialTheme.typography.displayLarge,
                color = Primary,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                MovieSection(
                    title = "🔥 Films Populaires",
                    movies = uiState.popularMovies,
                    onMovieClick = onMovieClick  // ← ajouté
                )
            }
        }

        item {
            MovieSection(
                title = "⭐ Mieux Notés",
                movies = uiState.topRatedMovies,
                onMovieClick = onMovieClick  // ← ajouté
            )
        }

        uiState.errorMessage?.let { error ->
            item {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = viewModel::retry,
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Réessayer")
                }
            }
        }
    }
}

@Composable
fun MovieSection(
    title: String,
    movies: List<Video>,
    onMovieClick: (Int) -> Unit = {}  // ← ajouté
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = OnBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                MovieCard(
                    movie = movie,
                    onMovieClick = onMovieClick  // ← ajouté
                )
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Video,
    onMovieClick: (Int) -> Unit = {}  // ← ajouté
) {
    Column(
        modifier = Modifier
            .width(130.dp)
            .clickable { onMovieClick(movie.id) }  // ← ajouté
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