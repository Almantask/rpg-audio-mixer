package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.settings.SettingsSyncPolicy

@Composable
fun ScenesScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "SCENES tab screen",
        body = "Your scene library will appear here.",
        modifier = modifier,
    )
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "Audio Library screen",
        body = "Your soundscape and FX libraries will appear here.",
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(
    onOpenTrash: () -> Unit,
    modifier: Modifier = Modifier,
    currentTimeMillis: () -> Long = { System.currentTimeMillis() },
) {
    val uriHandler = LocalUriHandler.current
    val syncPolicy = remember { SettingsSyncPolicy() }
    var lastSyncedAtMillis by rememberSaveable { mutableLongStateOf(0L) }
    val effectiveLastSyncedAtMillis = lastSyncedAtMillis.takeIf { it > 0L }
    val isSyncEnabled = syncPolicy.isSyncAvailable(
        currentTimeMillis = currentTimeMillis(),
        lastSyncedAtMillis = effectiveLastSyncedAtMillis,
    )

    ScreenContainer(
        modifier = modifier,
    ) {
        Text(
            text = "Behind the Screen",
            style = MaterialTheme.typography.headlineMedium,
        )
        Button(onClick = onOpenTrash) {
            Text("Restore Recent Deletes")
        }
        OutlinedButton(
            onClick = { lastSyncedAtMillis = currentTimeMillis() },
            enabled = isSyncEnabled,
        ) {
            Text("Sync Purchases & Free Tracks")
        }
        Text("Version 1.0")
        OutlinedButton(
            onClick = { uriHandler.openUri("https://github.com/Almantask/rpg-audio-mixer") },
        ) {
            Text("Open documentation")
        }
    }
}

@Composable
fun TrashScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "Recent Deletes",
        body = "Recently deleted items will appear here.",
        modifier = modifier,
    )
}

@Composable
fun CampaignSessionsScreen(
    campaignName: String,
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = if (campaignName.isBlank()) {
            "Campaign Sessions"
        } else {
            "Sessions list for $campaignName"
        },
        body = "Campaign sessions will appear here.",
        modifier = modifier,
    )
}

@Composable
private fun PlaceholderScreen(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    ScreenContainer(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        content()
    }
}
