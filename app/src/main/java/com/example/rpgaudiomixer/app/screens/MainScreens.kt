package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.BuildConfig

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "Home screen",
        body = "Active campaign, resume journey, and top tracks will appear here.",
        modifier = modifier,
    )
}

@Composable
fun CampaignsScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "Campaigns list screen",
        body = "Campaign management will be implemented in the next iteration.",
        modifier = modifier,
    )
}

@Composable
fun ScenesScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "SCENES tab screen",
        body = "Global scene management will be implemented in a later iteration.",
        modifier = modifier,
    )
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "Audio Library screen",
        body = "Soundscapes and sound effects will appear here.",
        modifier = modifier,
    )
}

@Composable
fun CreditsScreen(
    onOpenTrash: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var syncEnabled by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Behind the Screen",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Version ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(
            onClick = onOpenTrash,
        ) {
            Text(text = "Restore Recent Deletes")
        }
        Button(
            onClick = { syncEnabled = false },
            enabled = syncEnabled,
        ) {
            Text(text = "Sync Purchases & Free Tracks")
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "Documentation")
                Text(text = "Discord")
                Text(text = "Support")
                Text(text = "Email")
            }
        }
    }
}

@Composable
fun TrashScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "Recent Deletes",
        body = "Restorable items will appear here once soft delete is implemented.",
        modifier = modifier,
    )
}

@Composable
private fun PlaceholderScreen(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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
