package com.example.rpgaudiomixer.ui.fx

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
 * FX Edit screen for editing track name, tags, and deletion.
 *
 * Features:
 * - Edit track name
 * - Add/remove tags
 * - Delete track (soft-delete)
 * - Save changes
 */
@Composable
fun FxEditScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FxEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = when (val state = uiState) {
                    is FxEditUiState.Success -> "Edit ${state.track.name}"
                    else -> "Edit FX"
                },
                showBackArrow = true,
                onBackClick = {
                    viewModel.save()
                    onNavigateBack()
                },
                onGearClick = null
            )
        },
        modifier = modifier
    ) { padding ->
        when (val state = uiState) {
            is FxEditUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is FxEditUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .testTag("FxEdit_Content"),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name field
                    OutlinedTextField(
                        value = state.track.name,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("FxEdit_NameField")
                    )

                    // Tags section
                    Text(
                        text = "Tags",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Current tags
                    if (state.track.tags.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.testTag("FxEdit_TagsList")
                        ) {
                            items(state.track.tags) { tag ->
                                InputChip(
                                    selected = true,
                                    onClick = { viewModel.removeTag(tag) },
                                    label = { Text(tag) },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove tag"
                                        )
                                    },
                                    modifier = Modifier.testTag("FxEdit_Tag_$tag")
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No tags yet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("FxEdit_NoTags")
                        )
                    }

                    // Add tag section
                    TagSelector(
                        onTagSelected = { viewModel.addTag(it) }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Delete button
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("FxEdit_DeleteButton")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete")
                    }
                }
            }
            is FxEditUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { viewModel.clearError() }
                )
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete FX Track") },
                text = { Text("Are you sure you want to delete this track? It will be moved to the Trash.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.delete()
                            showDeleteDialog = false
                            onNavigateBack()
                        },
                        modifier = Modifier.testTag("FxEdit_DeleteDialog_Confirm")
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false },
                        modifier = Modifier.testTag("FxEdit_DeleteDialog_Cancel")
                    ) {
                        Text("Cancel")
                    }
                },
                modifier = Modifier.testTag("FxEdit_DeleteDialog")
            )
        }
    }
}

/**
 * Tag selector with predefined tags.
 */
@Composable
private fun TagSelector(
    onTagSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val predefinedTags = listOf(
        "Combat", "Exploration", "Tavern", "Nature",
        "Magic", "Horror", "Mystery", "Action"
    )

    var showCustomDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = "Add Tag",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("FxEdit_TagSelector")
        ) {
            items(predefinedTags) { tag ->
                SuggestionChip(
                    onClick = { onTagSelected(tag) },
                    label = { Text(tag) },
                    modifier = Modifier.testTag("FxEdit_TagOption_$tag")
                )
            }

            item {
                SuggestionChip(
                    onClick = { showCustomDialog = true },
                    label = { Text("Custom") },
                    icon = {
                        Icon(Icons.Default.Add, contentDescription = "Add custom tag")
                    },
                    modifier = Modifier.testTag("FxEdit_TagOption_Custom")
                )
            }
        }
    }

    if (showCustomDialog) {
        CustomTagDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { tag ->
                onTagSelected(tag)
                showCustomDialog = false
            }
        )
    }
}

/**
 * Dialog for adding a custom tag.
 */
@Composable
private fun CustomTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var customTag by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Tag") },
        text = {
            OutlinedTextField(
                value = customTag,
                onValueChange = { customTag = it },
                label = { Text("Tag Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("FxEdit_CustomTagDialog_Field")
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (customTag.isNotBlank()) onConfirm(customTag.trim()) },
                enabled = customTag.isNotBlank(),
                modifier = Modifier.testTag("FxEdit_CustomTagDialog_Confirm")
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("FxEdit_CustomTagDialog_Cancel")
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.testTag("FxEdit_CustomTagDialog")
    )
}
