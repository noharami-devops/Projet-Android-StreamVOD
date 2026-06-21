package com.groupe9.streamvod.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupe9.streamvod.ui.theme.*

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onMyVideosClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) onLogout()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "👤",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.email,
                style = MaterialTheme.typography.titleMedium,
                color = OnBackground
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        ProfileMenuItem(
            icon = Icons.Default.VideoLibrary,
            label = "Mes vidéos",
            onClick = onMyVideosClick
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfileMenuItem(
            icon = Icons.Default.History,
            label = "Historique",
            onClick = onHistoryClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = viewModel::logout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary
            )
        ) {
            Text(
                text = "Se déconnecter",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = label, color = OnBackground, style = MaterialTheme.typography.bodyLarge)
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}