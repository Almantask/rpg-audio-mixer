package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Home" },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Home", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun CampaignsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Campaigns" },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Campaigns", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun ScenesScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Scenes" },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Scenes", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Library" },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Library", style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
fun CreditsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = "Credits" },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Credits", style = MaterialTheme.typography.headlineLarge)
    }
}