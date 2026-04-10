package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog

@Composable
fun SoundscapeLibraryRoute(
    onNavigateToComposer: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SoundscapeLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    SoundscapeLibraryScreen(
        uiState = uiState,
        onNavigateToComposer = onNavigateToComposer,
        onNavigateBack = onNavigateBack,
        onCreateCategory = { showCreateDialog = true },
        onDeleteCategory = viewModel::deleteCategory,
        onErrorDismiss = viewModel::clearError
    )

    if (showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                viewModel.createCategory(name)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun SoundscapeLibraryScreen(
    uiState: SoundscapeLibraryUiState,
    onNavigateToComposer: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onCreateCategory: () -> Unit,
    onDeleteCategory: (Long) -> Unit,
    onErrorDismiss: () -> Unit
) {
    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Soundscape Library",
                showBackArrow = true,
                onBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateCategory) {
                Icon(Icons.Default.Add, contentDescription = "New Composition")
            }
        }
    ) { padding ->
        when (uiState) {
            is SoundscapeLibraryUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SoundscapeLibraryUiState.Success -> {
                if (uiState.categories.isEmpty()) {
                    EmptyLibraryState(
                        onCreateCategory = onCreateCategory,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.categories) { categoryWithCounts ->
                            CategoryCard(
                                categoryWithCounts = categoryWithCounts,
                                onEditClick = { onNavigateToComposer(categoryWithCounts.category.id) }
                            )
                        }
                    }
                }
            }

            is SoundscapeLibraryUiState.Error -> {
                ErrorDialog(
                    message = uiState.message,
                    onDismiss = onErrorDismiss
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    categoryWithCounts: CategoryWithCounts,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryWithCounts.category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrackCountLabel(
                    level = "I",
                    count = categoryWithCounts.level1Count
                )
                TrackCountLabel(
                    level = "II",
                    count = categoryWithCounts.level2Count
                )
                TrackCountLabel(
                    level = "III",
                    count = categoryWithCounts.level3Count
                )
            }
        }
    }
}

@Composable
private fun TrackCountLabel(
    level: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    val textColor = if (count == 0) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Text(
        text = "$level: $count",
        style = MaterialTheme.typography.bodySmall,
        color = textColor,
        modifier = modifier
    )
}

@Composable
private fun EmptyLibraryState(
    onCreateCategory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No soundscape categories yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            TextButton(
                onClick = onCreateCategory,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("CREATE NEW COMPOSITION")
            }
        }
    }
}

@Composable
private fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Soundscape Category") },
        text = {
            TextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                singleLine = true
            )
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
