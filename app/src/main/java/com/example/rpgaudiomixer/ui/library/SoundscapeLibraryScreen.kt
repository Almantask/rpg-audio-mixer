package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.LoadingStateView
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

@Composable
fun SoundscapeLibraryScreen(
    onOpenComposer: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundscapeLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is SoundscapeLibraryNavigation.OpenComposer -> {
                    onOpenComposer(event.categoryId, event.categoryName)
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Soundscape Library",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Select an atmospheric core to begin weaving your auditory tapestry.",
            style = MaterialTheme.typography.bodyLarge,
        )

        when (val state = uiState) {
            SoundscapeLibraryUiState.Loading -> LoadingStateView(label = "Loading soundscapes…")
            is SoundscapeLibraryUiState.Error -> ErrorDialog(message = state.message, onDismiss = { })
            is SoundscapeLibraryUiState.Success -> {
                if (state.isDemoDownloadVisible) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = viewModel::downloadDemoSoundscapes,
                        enabled = !state.isDownloadingDemoContent,
                    ) {
                        if (state.isDownloadingDemoContent) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                strokeWidth = 2.dp,
                            )
                        }
                        Text("Get Demo Soundscapes")
                    }
                }

                if (state.categories.isEmpty()) {
                    EmptyStateView(
                        title = "No soundscape categories yet",
                        actionLabel = "Create Category",
                        onAction = { showCreateDialog = true },
                        illustration = "🔮",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.categories, key = { category -> category.id }) { category ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.deleteCategory(category.id) },
                            ) {
                                SoundscapeCategoryCard(
                                    category = category,
                                    onOpen = { viewModel.openComposer(category.id, category.name) },
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showCreateDialog = true },
        ) {
            Text("Create Category")
        }
    }

    if (showCreateDialog) {
        CreateSoundscapeCategoryDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                viewModel.createCategory(name)
                showCreateDialog = false
            },
        )
    }
}

@Composable
private fun SoundscapeCategoryCard(
    category: SoundscapeCategory,
    onOpen: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = ArcanumSurfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    category.themeLabel?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = ArcanumGold,
                        )
                    }
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                IconButton(onClick = onOpen) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${category.name}",
                        tint = ArcanumGold,
                    )
                }
            }
            Text(
                text = "I: ${category.levelOneCount} · II: ${category.levelTwoCount} · III: ${category.levelThreeCount}",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun CreateSoundscapeCategoryDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Category") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Category name") },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name) },
                enabled = name.trim().isNotBlank(),
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
