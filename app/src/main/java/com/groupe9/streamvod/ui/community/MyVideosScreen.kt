package com.groupe9.streamvod.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupe9.streamvod.ui.theme.*

@Composable
fun MyVideosScreen(
    onBackClick: () -> Unit,
    onVideoClick: (String, String) -> Unit = { _, _ -> },
    viewModel: MyVideosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId = viewModel.currentUserId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
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
                text = "Mes vidéos",
                style = MaterialTheme.typography.titleMedium,
                color = OnBackground
            )
        }

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            uiState.videos.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Vous n'avez publié aucune vidéo", color = TextSecondary)
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.videos) { video ->
                        UserVideoCard(
                            video = video,
                            currentUserId = currentUserId,
                            onVideoClick = { onVideoClick(video.videoUrl, video.title) },
                            onLikeClick = {},
                            onDeleteClick = { viewModel.deleteVideo(video.id, video.uploaderId) }
                        )
                    }
                }
            }
        }
    }
}