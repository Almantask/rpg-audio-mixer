package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.theme.ArcanumBorder
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.EmptyState
import com.example.rpgaudiomixer.ui.components.PrimaryButton
import com.example.rpgaudiomixer.ui.components.SceneCard

@Composable
fun ScenesScreen(
    onOpenScene: (Long) -> Unit,
    onCredits: () -> Unit,
    viewModel: ScenesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(onCredits = onCredits)

        // Search bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchQueryChanged,
            placeholder = { Text("Search scenes…", color = ArcanumGrayLight) },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = ArcanumGrayLight) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        // Sort chips
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SceneSort.values().forEach { sort ->
                FilterChip(
                    selected = state.sort == sort,
                    onClick = { viewModel.onSortChanged(sort) },
                    label = {
                        Text(
                            text = when (sort) {
                                SceneSort.NAME -> "NAME"
                                SceneSort.LAST_USED -> "LAST USED"
                            },
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ArcanumGold,
                        selectedLabelColor = MaterialTheme.colorScheme.background,
                        containerColor = ArcanumCardSurface,
                        labelColor = ArcanumGrayLight,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = state.sort == sort,
                        borderColor = ArcanumBorder,
                        selectedBorderColor = ArcanumGold,
                    ),
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.scenes.isEmpty() && !state.isLoading) {
                item {
                    EmptyState(
                        title = "No Scenes Found",
                        subtitle = "Add a new scene to get started.",
                    )
                }
            }

            items(state.scenes, key = { it.id }) { scene ->
                SceneCard(
                    scene = scene,
                    onPlay = { onOpenScene(scene.id) },
                    onClick = { onOpenScene(scene.id) },
                )
            }

            item {
                PrimaryButton(
                    text = "+ ADD NEW SCENE",
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showAddDialog) {
        AddSceneDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, description ->
                viewModel.addScene(name, description)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun AddSceneDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Scene") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Scene Name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), description.trim()) },
            ) {
                Text("CREATE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        },
    )
}
