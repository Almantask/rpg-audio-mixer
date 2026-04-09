package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.BuildConfig

object MainScreenTestTags {
    const val HOME = "Screen_Home"
    const val CAMPAIGNS = "Screen_Campaigns"
    const val SCENES = "Screen_Scenes"
    const val LIBRARY = "Screen_Library"
    const val SETTINGS = "Screen_Settings"
    const val TRASH = "Screen_Trash"
    const val SETTINGS_RESTORE = "Settings_RestoreRecentDeletes"
    const val SETTINGS_SYNC = "Settings_SyncPurchases"
    const val SETTINGS_DOCUMENTATION = "Settings_DocumentationLink"
}

private const val SYNC_COOLDOWN_MILLIS = 24 * 60 * 60 * 1000L

@Composable
fun HomeScreen() {
    PlaceholderRootScreen(
        title = "Home screen",
        subtitle = "Welcome to Arcanum Audio.",
        tag = MainScreenTestTags.HOME,
    )
}

@Composable
fun PlaceholderCampaignsScreen() {
    PlaceholderRootScreen(
        title = "Campaigns list screen",
        subtitle = "Campaign management arrives in Iteration 1.",
        tag = MainScreenTestTags.CAMPAIGNS,
    )
}

@Composable
fun PlaceholderScenesScreen() {
    PlaceholderRootScreen(
        title = "SCENES tab screen",
        subtitle = "Global scene management arrives in Iteration 2.",
        tag = MainScreenTestTags.SCENES,
    )
}

@Composable
fun LibraryScreen() {
    PlaceholderRootScreen(
        title = "Audio Library screen",
        subtitle = "Soundscape and FX tooling arrive in later iterations.",
        tag = MainScreenTestTags.LIBRARY,
    )
}

@Composable
fun CreditsScreen(
    syncRepository: SettingsSyncRepository,
    onRestoreRecentDeletes: () -> Unit,
) {
    val lastSuccessfulSyncAt by syncRepository.lastSuccessfulSyncAtMillis.collectAsState()
    val now = System.currentTimeMillis()
    val syncAvailable = lastSuccessfulSyncAt?.let { now - it >= SYNC_COOLDOWN_MILLIS } ?: true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag(MainScreenTestTags.SETTINGS),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Behind the Screen",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Arcanum Audio",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Made with ❤ for GMs everywhere",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(MainScreenTestTags.SETTINGS_SYNC),
            onClick = { syncRepository.markSynced(System.currentTimeMillis()) },
            enabled = syncAvailable,
        ) {
            Text("Sync Purchases & Free Tracks")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(MainScreenTestTags.SETTINGS_RESTORE),
            onClick = onRestoreRecentDeletes,
        ) {
            Text("Restore Recent Deletes")
        }
        LinkRow(
            text = "Documentation",
            tag = MainScreenTestTags.SETTINGS_DOCUMENTATION,
        )
        LinkRow(text = "Discord community")
        LinkRow(text = "Contact / support email")
    }
}

@Composable
fun TrashScreen() {
    PlaceholderRootScreen(
        title = "Recent Deletes",
        subtitle = "Deleted items will appear here in a later iteration.",
        tag = MainScreenTestTags.TRASH,
    )
}

@Composable
private fun PlaceholderRootScreen(
    title: String,
    subtitle: String,
    tag: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag(tag),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun LinkRow(
    text: String,
    tag: String? = null,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(enabled = false) {}
            .padding(16.dp)
            .then(
                if (tag == null) {
                    Modifier
                } else {
                    Modifier.testTag(tag)
                },
            ),
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
    )
}
