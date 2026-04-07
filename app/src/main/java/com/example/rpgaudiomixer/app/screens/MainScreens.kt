package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home Screen - Coming Soon",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun CampaignsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Campaigns Screen - Coming Soon",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun ScenesScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Scenes Screen - Coming Soon",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Library Screen - Coming Soon",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
