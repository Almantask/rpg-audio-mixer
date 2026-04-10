package com.example.rpgaudiomixer.ui.credits

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteRestore
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.SettingsBackupRestore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.BuildConfig

private data class CreditsLink(
    val title: String,
    val icon: ImageVector,
    val destination: String,
)

@Composable
fun CreditsRoute(
    onOpenTrash: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val links = listOf(
        CreditsLink(
            title = "Documentation",
            icon = Icons.Rounded.Description,
            destination = "https://github.com/Almantask/rpg-audio-mixer",
        ),
        CreditsLink(
            title = "Discord community",
            icon = Icons.Rounded.Forum,
            destination = "https://discord.com",
        ),
        CreditsLink(
            title = "Contact / support email",
            icon = Icons.Rounded.Email,
            destination = "mailto:support@arcanumaudio.app",
        ),
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Arcanum Audio",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Version ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Made with ❤️ for GMs everywhere",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    onClick = {},
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SettingsBackupRestore,
                        contentDescription = null,
                    )
                    Text(
                        text = "SYNC PURCHASES & FREE TRACKS",
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                Text(
                    text = "Available once per day",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenTrash,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteRestore,
                        contentDescription = null,
                    )
                    Text(
                        text = "RESTORE RECENT DELETES",
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
        item {
            CreditsSection(
                title = "CREDITS",
                lines = listOf(
                    "Design & Development — Almantask",
                    "Built for atmosphere-heavy tabletop sessions with modern Android craft.",
                ),
            )
        }
        item {
            Text(
                text = "LINKS",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        items(links) { link ->
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(link.destination)),
                    )
                },
            ) {
                Icon(imageVector = link.icon, contentDescription = null)
                Text(
                    text = link.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun CreditsSection(
    title: String,
    lines: List<String>,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            lines.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
