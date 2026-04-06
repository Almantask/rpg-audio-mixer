package com.example.rpgaudiomixer.ui.library.soundscapes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

@Composable
fun SoundscapesLibraryTab(
    onEditCategory: (Long) -> Unit = {},
    viewModel: SoundscapeLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is SoundscapeLibraryUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is SoundscapeLibraryUiState.Success -> {
                SoundscapeCategoryList(
                    categories = state.categories,
                    onEdit = onEditCategory,
                    onDelete = { viewModel.deleteCategory(it) },
                    onAddNew = { showCreateDialog = true },
                )
            }
            is SoundscapeLibraryUiState.Error -> {
                EmptyStateView(
                    message = "No soundscape categories yet.",
                    actionLabel = "+ New Composition",
                    onAction = { showCreateDialog = true },
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateCategoryDialog(
            onConfirm = { name ->
                viewModel.createCategory(name)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }
}

@Composable
private fun SoundscapeCategoryList(
    categories: List<SoundscapeCategory>,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onAddNew: () -> Unit,
) {
    if (categories.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            EmptyStateView(
                message = "No soundscape categories yet.",
                actionLabel = "+ New Composition",
                onAction = onAddNew,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(categories, key = { it.id }) { category ->
            SwipeToDeleteContainer(
                onDelete = { onDelete(category.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                SoundscapeCategoryCard(
                    category = category,
                    onEdit = { onEdit(category.id) },
                )
            }
        }
        item {
            Button(
                onClick = onAddNew,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
            ) {
                Text("+ New Composition", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun SoundscapeCategoryCard(
    category: SoundscapeCategory,
    onEdit: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcanumGold,
                )
                val counts = IntensityLevel.entries.map { level ->
                    val count = category.tracks.count { it.intensityLevel == level }
                    "${level.label}: $count"
                }.joinToString("  ")
                Text(
                    text = counts,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit category",
                    tint = ArcanumGold,
                )
            }
        }
    }
}

@Composable
private fun CreateCategoryDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Soundscape Category") },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Category name") },
                singleLine = true,
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                enabled = name.isNotBlank(),
            ) { Text("Create") }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
