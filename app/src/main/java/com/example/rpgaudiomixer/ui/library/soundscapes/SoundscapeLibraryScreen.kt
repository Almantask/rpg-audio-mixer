package com.example.rpgaudiomixer.ui.library.soundscapes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.common.UiState

@Composable
fun SoundscapeLibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: SoundscapeLibraryViewModel = hiltViewModel(),
    onNavigateToComposer: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyStateView(
                        title = "No Soundscapes Yet",
                        message = "Create your first soundscape composition to get started",
                        actionLabel = "+ NEW COMPOSITION",
                        onAction = { showCreateDialog = true }
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.data) { categoryWithCounts ->
                            SoundscapeCategoryCard(
                                category = categoryWithCounts.category,
                                trackCountByLevel = categoryWithCounts.trackCountByLevel,
                                onEditClick = { onNavigateToComposer(categoryWithCounts.category.id) }
                            )
                        }
                    }
                }
            }
            is UiState.Error -> {
                errorMessage = state.message
                EmptyStateView(
                    title = "Error Loading Soundscapes",
                    message = state.message,
                    actionLabel = "Retry",
                    onAction = { /* viewModel.loadCategories() */ }
                )
            }
        }

        // FAB for creating new composition
        if (uiState is UiState.Success) {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Composition")
            }
        }
    }

    // Create category dialog
    if (showCreateDialog) {
        CreateSoundscapeCategoryDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                viewModel.createCategory(name)
                showCreateDialog = false
            }
        )
    }

    // Error dialog
    errorMessage?.let { message ->
        ErrorDialog(
            message = message,
            onDismiss = { errorMessage = null }
        )
    }
}

@Composable
private fun SoundscapeCategoryCard(
    category: com.example.rpgaudiomixer.domain.model.SoundscapeCategory,
    trackCountByLevel: Map<com.example.rpgaudiomixer.domain.model.IntensityLevel, Int>,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        onClick = onEditClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                com.example.rpgaudiomixer.domain.model.IntensityLevel.entries.forEach { level ->
                    val count = trackCountByLevel[level] ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Level ${level.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (count == 0) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                   else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (count == 0) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                   else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            TextButton(onClick = onEditClick) {
                Text("✏️ EDIT")
            }
        }
    }
}

@Composable
private fun CreateSoundscapeCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Soundscape Composition") },
        text = {
            Column {
                Text("Enter a name for your new soundscape category:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(categoryName) },
                enabled = categoryName.isNotBlank()
            ) {
                Text("CREATE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
