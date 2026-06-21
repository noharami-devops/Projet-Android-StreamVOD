package com.groupe9.streamvod.ui.community

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupe9.streamvod.domain.model.UserVideo
import com.groupe9.streamvod.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun CommunityScreen(
    onVideoClick: (String) -> Unit = {},
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showUploadDialog by remember { mutableStateOf(false) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // Pour afficher les erreurs visiblement
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedVideoUri = it
            showUploadDialog = true
        }
    }

    LaunchedEffect(uiState.uploadSuccess) {
        if (uiState.uploadSuccess) {
            showUploadDialog = false
            selectedVideoUri = null
            viewModel.resetUploadState()
            scope.launch {
                snackbarHostState.showSnackbar("Vidéo publiée avec succès !")
            }
        }
    }

    // ⚠️ NOUVEAU : afficher l'erreur dès qu'elle apparaît, peu importe où
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar("Erreur : $message")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎥 Communauté",
                        style = MaterialTheme.typography.displayLarge,
                        color = Primary
                    )
                    FloatingActionButton(
                        onClick = { videoPicker.launch("video/*") },
                        containerColor = Primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Uploader une vidéo", tint = OnPrimary)
                    }
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
                                Text("Aucune vidéo pour l'instant", color = TextSecondary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Soyez le premier à partager !", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
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
                                    onVideoClick = onVideoClick,
                                    onLikeClick = { viewModel.toggleLike(video.id, video.likes) }
                                )
                            }
                        }
                    }
                }
            }

            if (showUploadDialog && selectedVideoUri != null) {
                UploadVideoDialog(
                    isUploading = uiState.isUploading,
                    onUpload = { title, description ->
                        viewModel.uploadVideo(
                            context = context,
                            uri = selectedVideoUri!!,
                            title = title,
                            description = description
                        )
                    },
                    onDismiss = {
                        showUploadDialog = false
                        selectedVideoUri = null
                        viewModel.resetUploadState()
                    }
                )
            }
        }
    }
}

@Composable
fun UserVideoCard(
    video: UserVideo,
    onVideoClick: (String) -> Unit,
    onLikeClick: () -> Unit
) {
    Card(
        onClick = { onVideoClick(video.videoUrl) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (video.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Par ${video.uploaderName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLikeClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Favorite, contentDescription = "Like", tint = Primary, modifier = Modifier.size(18.dp))
                    }
                    Text(text = "${video.likes}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun UploadVideoDialog(
    isUploading: Boolean,
    onUpload: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        title = { Text("Publier une vidéo") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre *") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary,
                        cursorColor = Primary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedLabelColor = Primary,
                        cursorColor = Primary
                    )
                )
                if (isUploading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Upload en cours...", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Primary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onUpload(title, description) },
                enabled = !isUploading && title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = OnPrimary, modifier = Modifier.size(16.dp))
                } else {
                    Text("Publier")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isUploading) {
                Text("Annuler", color = TextSecondary)
            }
        }
    )
}