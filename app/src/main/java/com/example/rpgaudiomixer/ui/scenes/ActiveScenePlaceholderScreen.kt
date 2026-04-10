package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.theme.ArcanumGold

@Composable
fun ActiveScenePlaceholderRoute(
    onTitleChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActiveScenePlaceholderViewModel = hiltViewModel(),
) {
    val scene by viewModel.scene.collectAsStateWithLifecycle()

    LaunchedEffect(scene?.name) {
        onTitleChange(scene?.name)
    }
    DisposableEffect(Unit) {
        onDispose { onTitleChange(null) }
    }

    ActiveScenePlaceholderScreen(
        sceneName = scene?.name ?: "Active Scene",
        autoplay = viewModel.autoplay,
        modifier = modifier,
    )
}

@Composable
private fun ActiveScenePlaceholderScreen(
    sceneName: String,
    autoplay: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = sceneName,
                style = MaterialTheme.typography.headlineMedium,
                color = ArcanumGold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = if (autoplay) {
                    "Playback will be wired here in the next iteration. This launch requested autoplay."
                } else {
                    "Playback controls and scene audio will be wired here in the next iteration."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        }
    }
}
