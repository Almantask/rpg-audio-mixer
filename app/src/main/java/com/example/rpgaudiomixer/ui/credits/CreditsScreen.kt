package com.example.rpgaudiomixer.ui.credits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTrash: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CreditsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Credits") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // App Info
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ARCANUM AUDIO",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Developer Info
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "DEVELOPER",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Built with ❤️ for Game Masters",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Links
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "LINKS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { /* Open docs */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Documentation")
                        }
                        TextButton(
                            onClick = { /* Open Discord */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Discord Community")
                        }
                        TextButton(
                            onClick = { /* Open email */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Contact Support")
                        }
                    }
                }
            }

            // Actions
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = onNavigateToTrash,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("RESTORE RECENT DELETES")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { /* Sync purchases - disabled for now */ },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false
                        ) {
                            Text("SYNC PURCHASES (Coming Soon)")
                        }
                    }
                }
            }
        }
    }
}
