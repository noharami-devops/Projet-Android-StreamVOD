package com.groupe9.streamvod.ui.community

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupe9.streamvod.domain.model.UserVideo
import com.groupe9.streamvod.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CommunityScreen(
    onVideoClick: (String) -> Unit = {},
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showUploadDialog by remember { mutableStateOf(false) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var showSourceDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Pour afficher les erreurs visiblement
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 1. Choisir depuis la galerie
    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedVideoUri = it
            showUploadDialog = true
        }
    }

    // 2. Filmer avec la caméra
    var cameraVideoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && cameraVideoUri != null) {
            selectedVideoUri = cameraVideoUri
            showUploadDialog = true
        }
    }

    fun launchCamera() {
        val videoFile = File(context.cacheDir, "camera_${System.currentTimeMillis()}.mp4")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            videoFile
        )
        cameraVideoUri = uri
        cameraLauncher.launch(uri)
    }

    // 2.bis Demande de permission caméra (obligatoire avant d'ouvrir la caméra)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Permission caméra refusée")
            }
        }
    }

    fun checkCameraPermissionAndLaunch() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(permission)
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

    // afficher l'erreur dès qu'elle apparaît, peu importe où
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
                        onClick = { showSourceDialog = true },
                        containerColor = Primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter une vidéo", tint = OnPrimary)
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
                                    currentUserId = viewModel.currentUserId,
                                    onVideoClick = onVideoClick,
                                    onLikeClick = { viewModel.toggleLike(video.id, video.likes) },
                                    onDeleteClick = { viewModel.deleteVideo(video.id, video.uploaderId) }
                                )
                            }
                        }
                    }
                }
            }

            // Dialog de choix : Galerie ou Caméra
            if (showSourceDialog) {
                AlertDialog(
                    onDismissRequest = { showSourceDialog = false },
                    title = { Text("Ajouter une vidéo") },
                    text = { Text("Choisissez une source") },
                    confirmButton = {
                        TextButton(onClick = {
                            showSourceDialog = false
                            checkCameraPermissionAndLaunch()
                        }) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Filmer", color = Primary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showSourceDialog = false
                            videoPicker.launch("video/*")
                        }) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Galerie", color = Primary)
                        }
                    }
                )
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
    currentUserId: String?,
    onVideoClick: (String) -> Unit,
    onLikeClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val isOwner = currentUserId != null && currentUserId == video.uploaderId

    Card(
        onClick = { onVideoClick(video.videoUrl) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (isOwner) {
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
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

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer la vidéo ?") },
            text = { Text("Cette action est irréversible.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDeleteClick()
                }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            }
        )
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