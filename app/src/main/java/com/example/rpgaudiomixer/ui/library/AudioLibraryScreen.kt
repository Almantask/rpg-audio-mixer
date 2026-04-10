package com.example.rpgaudiomixer.ui.library

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.BentoCard
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.ui.UiState
import com.example.rpgaudiomixer.ui.fx.FxLibraryTabRoute

private enum class LibraryTab(
    val label: String,
) {
    SOUNDSCAPES("Soundscapes"),
    SOUND_EFFECTS("Sound Effects"),
}

@Composable
fun AudioLibraryRoute(
    onOpenComposer: (Long?, String) -> Unit,
    onTitleChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(LibraryTab.SOUNDSCAPES.ordinal) }

    DisposableEffect(Unit) {
        onTitleChange(null)
        onDispose { onTitleChange(null) }
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        SecondaryScrollableTabRow(selectedTabIndex = selectedTab) {
            LibraryTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = index == selectedTab,
                    onClick = { selectedTab = index },
                    text = { Text(tab.label) },
                )
            }
        }

        when (LibraryTab.entries[selectedTab]) {
            LibraryTab.SOUNDSCAPES -> SoundscapeLibraryTabRoute(
                onOpenComposer = onOpenComposer,
                modifier = Modifier.weight(1f),
            )

            LibraryTab.SOUND_EFFECTS -> FxLibraryTabRoute(
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SoundscapeLibraryTabRoute(
    onOpenComposer: (Long?, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundscapeLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    SoundscapeLibraryScreen(
        uiState = uiState,
        errorMessage = errorMessage,
        onOpenComposer = onOpenComposer,
        onDeleteCategory = viewModel::deleteCategory,
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@Composable
private fun SoundscapeLibraryScreen(
    uiState: UiState<List<SoundscapeCategory>>,
    errorMessage: String?,
    onOpenComposer: (Long?, String) -> Unit,
    onDeleteCategory: (Long) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.LibraryMusic,
                    title = "Unable to load soundscapes",
                    body = uiState.message,
                    actionLabel = "Dismiss",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                SoundscapeLibraryContent(
                    categories = uiState.data,
                    onCreateClick = { showCreateDialog = true },
                    onOpenComposer = onOpenComposer,
                    onDeleteCategory = onDeleteCategory,
                )
            }
        }

        if (showCreateDialog) {
            CreateCategoryDialog(
                onDismiss = { showCreateDialog = false },
                onCreateCategory = { categoryName ->
                    showCreateDialog = false
                    onOpenComposer(null, categoryName)
                },
            )
        }
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = onDismissError,
    )
}

@Composable
private fun SoundscapeLibraryContent(
    categories: List<SoundscapeCategory>,
    onCreateClick: () -> Unit,
    onOpenComposer: (Long?, String) -> Unit,
    onDeleteCategory: (Long) -> Unit,
) {
    if (categories.isEmpty()) {
        EmptyStateView(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            icon = Icons.Rounded.LibraryMusic,
            title = "No soundscape categories yet",
            body = "Create your first composition to start building atmospheric layers.",
            actionLabel = "New Composition",
            onAction = onCreateClick,
        )
        return
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Soundscape Library",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Select an atmospheric core and refine its intensity layers in the composer.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 220.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(categories, key = { category -> category.id }) { category ->
                SwipeToDeleteContainer(
                    onDelete = { onDeleteCategory(category.id) },
                ) {
                    BentoCard(
                        onClick = { onOpenComposer(category.id, category.name) },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = category.themeLabel ?: "CUSTOM",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            IconButton(
                                onClick = { onOpenComposer(category.id, category.name) },
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Edit category",
                                )
                            }
                        }
                        IntensityCountRow(category = category)
                    }
                }
            }
            item {
                BentoCard(
                    onClick = onCreateClick,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                        )
                        Text(
                            text = "NEW COMPOSITION",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IntensityCountRow(
    category: SoundscapeCategory,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IntensityCountText(label = "I", count = category.levelOneTrackCount)
        IntensityCountText(label = "II", count = category.levelTwoTrackCount)
        IntensityCountText(label = "III", count = category.levelThreeTrackCount)
    }
}

@Composable
private fun IntensityCountText(
    label: String,
    count: Int,
) {
    Text(
        text = "$label: ${count.toString().padStart(2, '0')}",
        style = MaterialTheme.typography.labelLarge,
        color = if (count == 0) {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.onSurface
        },
    )
}

@Composable
private fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onCreateCategory: (String) -> Unit,
) {
    var categoryName by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Composition") },
        text = {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category name") },
                singleLine = true,
            )
        },
        confirmButton = {
            Button(
                onClick = { onCreateCategory(categoryName.trim()) },
                enabled = categoryName.isNotBlank(),
            ) {
                Text("Open Composer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
