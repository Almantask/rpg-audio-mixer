package com.example.rpgaudiomixer.ui.soundscapelibrary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

@Composable
fun SoundscapeLibraryScreen(
    onCategoryClick: (String) -> Unit,
    viewModel: SoundscapeLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showCreateDialog by viewModel.showCreateDialog.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (uiState is SoundscapeLibraryUiState.Success) {
                FloatingActionButton(
                    onClick = { viewModel.showCreateDialog() },
                    modifier = Modifier.testTag("SoundscapeLibraryScreen_FAB"),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Composition")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (uiState) {
                is SoundscapeLibraryUiState.Loading -> {
                    LoadingContent()
                }
                is SoundscapeLibraryUiState.Success -> {
                    val categories = (uiState as SoundscapeLibraryUiState.Success).categories
                    if (categories.isEmpty()) {
                        EmptyStateContent(onCreateClick = { viewModel.showCreateDialog() })
                    } else {
                        CategoriesList(
                            categories = categories,
                            onCategoryClick = onCategoryClick,
                            onDeleteClick = { viewModel.deleteCategory(it) }
                        )
                    }
                }
                is SoundscapeLibraryUiState.Error -> {
                    ErrorContent(message = (uiState as SoundscapeLibraryUiState.Error).message)
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCategoryDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onConfirm = { name -> viewModel.createCategory(name) }
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.testTag("SoundscapeLibrary_Loading"))
    }
}

@Composable
private fun EmptyStateContent(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Soundscape Categories",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCreateClick,
            modifier = Modifier.testTag("SoundscapeLibrary_EmptyState_CreateButton")
        ) {
            Text("Create New Composition")
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.testTag("SoundscapeLibrary_ErrorTitle")
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("SoundscapeLibrary_ErrorMessage")
        )
    }
}

@Composable
private fun CategoriesList(
    categories: List<SoundscapeCategory>,
    onCategoryClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("SoundscapeLibrary_CategoriesList"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id) },
                onDeleteClick = { onDeleteClick(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: SoundscapeCategory,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("CategoryCard_${category.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IntensityLevel.entries.forEach { level ->
                        val count = category.trackCountByLevel[level] ?: 0
                        Text(
                            text = "${level.displayName}: $count",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (count > 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
            IconButton(
                onClick = onClick,
                modifier = Modifier.testTag("CategoryCard_${category.id}_EditButton")
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Soundscape Category") },
        text = {
            Column {
                Text("Enter a name for the new category:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CreateCategoryDialog_NameField"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onConfirm(categoryName.trim())
                    }
                },
                enabled = categoryName.isNotBlank(),
                modifier = Modifier.testTag("CreateCategoryDialog_ConfirmButton")
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("CreateCategoryDialog_CancelButton")
            ) {
                Text("Cancel")
            }
        }
    )
}
