package com.groupe9.streamvod.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayDisabled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupe9.streamvod.ui.theme.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun PlayerScreen(
    movieId: Int,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = OnBackground
                )
            }
            Text(
                text = uiState.title,
                style = MaterialTheme.typography.titleMedium,
                color = OnBackground
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = Primary)
                }
                uiState.hasError || uiState.videoKey == null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayDisabled,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bande-annonce non disponible",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> {
                    AndroidView(
                        factory = { context ->
                            YouTubePlayerView(context).apply {
                                enableAutomaticInitialization = false  // ← ajouter cette ligne

                                val options = com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions.Builder()
                                    .controls(1)
                                    .build()

                                initialize(object : AbstractYouTubePlayerListener() {
                                    override fun onReady(youTubePlayer: YouTubePlayer) {
                                        println("DEBUG YOUTUBE READY, loading key: ${uiState.videoKey}")
                                        youTubePlayer.loadVideo(uiState.videoKey!!, 0f)
                                    }

                                    override fun onError(
                                        youTubePlayer: YouTubePlayer,
                                        error: PlayerConstants.PlayerError
                                    ) {
                                        println("DEBUG YOUTUBE ERROR: $error")
                                        viewModel.onPlayerError()
                                    }
                                }, options)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Section infos sous le player
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🎬 Bande-annonce du film",
                style = MaterialTheme.typography.titleMedium,
                color = OnBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (uiState.hasError || uiState.videoKey == null)
                    "Le propriétaire de cette vidéo a désactivé la lecture intégrée, ou aucune bande-annonce n'est disponible pour ce film."
                else
                    "Lecture de la bande-annonce officielle.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}