package com.groupe9.streamvod.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.groupe9.streamvod.ui.theme.Background
import com.groupe9.streamvod.ui.theme.Primary

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎬 Bienvenue sur StreamVOD",
            style = MaterialTheme.typography.titleLarge,
            color = Primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Connexion réussie !",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}