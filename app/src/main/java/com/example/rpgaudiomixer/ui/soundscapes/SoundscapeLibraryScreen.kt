package com.example.rpgaudiomixer.ui.soundscapes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog

/**
 * Soundscape Library screen showing all soundscape categories.
 *
 * Features:
 * - Scrollable list of category cards with track counts
 * - Empty state with "Invoke Your First Category" prompt
 * - FAB to create new category
 * - Swipe-to-delete categories
 * - Navigate to Category Composer on card tap or edit button
 */
@Composable
fun SoundscapeLibraryScreen(
    onNavigateToCategoryComposer: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundscapeLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Soundscape Library",
                showBackArrow = false,
                onGearClick = onNavigateToSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.testTag("SoundscapeLibrary_CreateFab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Category")
            }
        },
        modifier = modifier
    ) { padding ->
        when (val state = uiState) {
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
                if (state.categories.isEmpty()) {
                    EmptySoundscapeLibraryState(
                        onCreateClick = { showCreateDialog = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .testTag("SoundscapeLibrary_List"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.categories,
                            key = { it.category.id }
                        ) { categoryWithCounts ->
                            SoundscapeCategoryCard(
                                categoryWithCounts = categoryWithCounts,
                                onClick = { onNavigateToCategoryComposer(categoryWithCounts.category.id) },
                                onEdit = { onNavigateToCategoryComposer(categoryWithCounts.category.id) },
                                onDelete = { viewModel.deleteCategory(categoryWithCounts.category.id) }
                            )
                        }
                    }
                }
            }
            is SoundscapeLibraryUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { viewModel.clearError() }
                )
            }
        }

        if (showCreateDialog) {
            CreateCategoryDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name ->
                    viewModel.createCategory(name)
                    showCreateDialog = false
                }
            )
        }
    }
}

/**
 * Empty state shown when no soundscape categories exist.
 */
@Composable
private fun EmptySoundscapeLibraryState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.testTag("SoundscapeLibrary_EmptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No soundscape categories yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Invoke your first category to begin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateClick,
            modifier = Modifier.testTag("SoundscapeLibrary_EmptyState_CreateButton")
        ) {
            Text("Create Category")
        }
    }
}

/**
 * Dialog for creating a new soundscape category.
 */
@Composable
private fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Soundscape Category") },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("CreateCategoryDialog_NameField")
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (categoryName.isNotBlank()) onCreate(categoryName.trim()) },
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
        },
        modifier = modifier.testTag("CreateCategoryDialog")
    )
}
