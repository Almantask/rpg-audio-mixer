package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.MasterSlider
import com.example.rpgaudiomixer.app.components.MultiSelectPickerSheet
import com.example.rpgaudiomixer.app.components.SoundscapeCategoryCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.ui.UiState

@Composable
fun ActiveSceneSoundscapesRoute(
    onTitleChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneSoundscapesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        val content = (uiState as? UiState.Success)?.data
        onTitleChange(content?.sceneName)
    }
    DisposableEffect(Unit) {
        onDispose { onTitleChange(null) }
    }

    ActiveSceneSoundscapesScreen(
        uiState = uiState,
        errorMessage = errorMessage,
        onSetMasterVolume = viewModel::setMasterVolume,
        onToggleCategoryPlayback = viewModel::toggleCategoryPlayback,
        onRollRandom = viewModel::rollRandom,
        onSetIntensity = viewModel::setIntensity,
        onSetMix = viewModel::setMix,
        onRemoveCategory = viewModel::removeCategory,
        onAddCategories = viewModel::addCategories,
        onMoveCategoryUp = { categoryId -> viewModel.moveCategory(categoryId, -1) },
        onMoveCategoryDown = { categoryId -> viewModel.moveCategory(categoryId, 1) },
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@Composable
private fun ActiveSceneSoundscapesScreen(
    uiState: UiState<ActiveSceneSoundscapesContent>,
    errorMessage: String?,
    onSetMasterVolume: (Float) -> Unit,
    onToggleCategoryPlayback: (Long) -> Unit,
    onRollRandom: (Long) -> Unit,
    onSetIntensity: (Long, com.example.rpgaudiomixer.domain.model.IntensityLevel) -> Unit,
    onSetMix: (Long, Float) -> Unit,
    onRemoveCategory: (Long) -> Unit,
    onAddCategories: (List<Long>) -> Unit,
    onMoveCategoryUp: (Long) -> Unit,
    onMoveCategoryDown: (Long) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddSheet by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.GraphicEq,
                    title = "Unable to load scene soundscapes",
                    body = uiState.message,
                    actionLabel = "Dismiss",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                ActiveSceneSoundscapesContentBody(
                    content = uiState.data,
                    onSetMasterVolume = onSetMasterVolume,
                    onToggleCategoryPlayback = onToggleCategoryPlayback,
                    onRollRandom = onRollRandom,
                    onSetIntensity = onSetIntensity,
                    onSetMix = onSetMix,
                    onRemoveCategory = onRemoveCategory,
                    onOpenAddSheet = { showAddSheet = true },
                    onMoveCategoryUp = onMoveCategoryUp,
                    onMoveCategoryDown = onMoveCategoryDown,
                )

                if (showAddSheet) {
                    MultiSelectPickerSheet(
                        title = "Add Soundscapes",
                        options = uiState.data.availableCategoryOptions,
                        onDismiss = { showAddSheet = false },
                        onConfirm = { selectedIds ->
                            onAddCategories(selectedIds)
                            showAddSheet = false
                        },
                    )
                }
            }
        }
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = onDismissError,
    )
}

@Composable
private fun ActiveSceneSoundscapesContentBody(
    content: ActiveSceneSoundscapesContent,
    onSetMasterVolume: (Float) -> Unit,
    onToggleCategoryPlayback: (Long) -> Unit,
    onRollRandom: (Long) -> Unit,
    onSetIntensity: (Long, com.example.rpgaudiomixer.domain.model.IntensityLevel) -> Unit,
    onSetMix: (Long, Float) -> Unit,
    onRemoveCategory: (Long) -> Unit,
    onOpenAddSheet: () -> Unit,
    onMoveCategoryUp: (Long) -> Unit,
    onMoveCategoryDown: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ActiveSceneTabs()
        }
        item {
            MasterSlider(
                label = "Master Atmosphere",
                volume = content.masterVolume,
                onVolumeChanged = onSetMasterVolume,
            )
        }

        if (content.categories.isEmpty()) {
            item {
                EmptyStateView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    icon = Icons.Rounded.QueueMusic,
                    title = "No soundscapes in this scene",
                    body = "Add categories from your library to start mixing the atmosphere.",
                    actionLabel = "Add New Soundscape",
                    onAction = onOpenAddSheet,
                )
            }
        } else {
            items(
                items = content.categories,
                key = { category -> category.categoryId },
            ) { category ->
                SwipeToDeleteContainer(
                    onDelete = { onRemoveCategory(category.categoryId) },
                ) {
                    SoundscapeCategoryCard(
                        model = category,
                        onRollRandom = { onRollRandom(category.categoryId) },
                        onTogglePlayback = { onToggleCategoryPlayback(category.categoryId) },
                        onMixChanged = { mixVolume -> onSetMix(category.categoryId, mixVolume) },
                        onIntensitySelected = { intensity ->
                            onSetIntensity(category.categoryId, intensity)
                        },
                        onMoveUp = { onMoveCategoryUp(category.categoryId) },
                        onMoveDown = { onMoveCategoryDown(category.categoryId) },
                    )
                }
            }
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenAddSheet,
            ) {
                Text("+ ADD NEW SOUNDSCAPE")
            }
        }
    }
}

@Composable
private fun ActiveSceneTabs() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Button(
            onClick = {},
        ) {
            Text("Soundscapes")
        }
        TextButton(
            onClick = {},
            enabled = false,
        ) {
            Text(
                text = "Soundboard",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
