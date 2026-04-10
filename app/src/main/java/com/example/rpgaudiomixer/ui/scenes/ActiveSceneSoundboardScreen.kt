package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.rpgaudiomixer.app.components.ActiveSceneTab
import com.example.rpgaudiomixer.app.components.ActiveSceneTabShell
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.FlamesDeleteZone
import com.example.rpgaudiomixer.app.components.FxButton
import com.example.rpgaudiomixer.app.components.MasterSlider
import com.example.rpgaudiomixer.app.components.MultiSelectPickerSheet
import com.example.rpgaudiomixer.ui.UiState

@Composable
fun ActiveSceneSoundboardRoute(
    onTitleChange: (String?) -> Unit,
    onOpenSoundscapes: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneSoundboardViewModel = hiltViewModel(),
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

    ActiveSceneSoundboardScreen(
        uiState = uiState,
        errorMessage = errorMessage,
        onOpenSoundscapes = onOpenSoundscapes,
        onSetMasterVolume = viewModel::setMasterVolume,
        onTriggerFx = viewModel::triggerFx,
        onStopFx = viewModel::stopFx,
        onAddFx = viewModel::addFx,
        onRemoveFx = viewModel::removeFx,
        onMoveFxUp = { fxTrackId -> viewModel.moveFx(fxTrackId, -1) },
        onMoveFxDown = { fxTrackId -> viewModel.moveFx(fxTrackId, 1) },
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@Composable
private fun ActiveSceneSoundboardScreen(
    uiState: UiState<ActiveSceneSoundboardContent>,
    errorMessage: String?,
    onOpenSoundscapes: () -> Unit,
    onSetMasterVolume: (Float) -> Unit,
    onTriggerFx: (Long) -> Unit,
    onStopFx: (Long) -> Unit,
    onAddFx: (List<Long>) -> Unit,
    onRemoveFx: (Long) -> Unit,
    onMoveFxUp: (Long) -> Unit,
    onMoveFxDown: (Long) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddSheet by rememberSaveable { mutableStateOf(false) }
    var pendingDeleteFxId by rememberSaveable { mutableStateOf<Long?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.GraphicEq,
                    title = "Unable to load scene soundboard",
                    body = uiState.message,
                    actionLabel = "Dismiss",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                val pendingDeleteFx = uiState.data.effects.firstOrNull { fx ->
                    fx.fxTrackId == pendingDeleteFxId
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    ActiveSceneTabShell(
                        activeTab = ActiveSceneTab.SOUNDBOARD,
                        onSelectSoundscapes = onOpenSoundscapes,
                        onSelectSoundboard = {},
                    )

                    MasterSlider(
                        label = "Master Volume",
                        volume = uiState.data.masterVolume,
                        onVolumeChanged = onSetMasterVolume,
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        if (uiState.data.effects.isEmpty()) {
                            EmptyStateView(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                icon = Icons.Rounded.LibraryMusic,
                                title = "No effects in this scene",
                                body = "Add effects from your library to start your soundboard.",
                                actionLabel = "Add New Effect",
                                onAction = { showAddSheet = true },
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 96.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(
                                    items = uiState.data.effects,
                                    key = { fx -> fx.fxTrackId },
                                ) { fx ->
                                    FxButton(
                                        model = fx,
                                        onTrigger = { onTriggerFx(fx.fxTrackId) },
                                        onStop = { onStopFx(fx.fxTrackId) },
                                        onMoveUp = { onMoveFxUp(fx.fxTrackId) },
                                        onMoveDown = { onMoveFxDown(fx.fxTrackId) },
                                        onArmDelete = { pendingDeleteFxId = fx.fxTrackId },
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            pendingDeleteFx?.let { fx ->
                                FlamesDeleteZone(
                                    itemName = fx.name,
                                    onConfirmDelete = {
                                        onRemoveFx(fx.fxTrackId)
                                        pendingDeleteFxId = null
                                    },
                                    onDismiss = { pendingDeleteFxId = null },
                                )
                            }

                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { showAddSheet = true },
                            ) {
                                androidx.compose.material3.Text("+ ADD NEW EFFECT")
                            }
                        }
                    }
                }

                if (showAddSheet) {
                    MultiSelectPickerSheet(
                        title = "Add Effects",
                        options = uiState.data.availableFxOptions,
                        onDismiss = { showAddSheet = false },
                        onConfirm = { selectedIds ->
                            onAddFx(selectedIds)
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
