package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.rpgaudiomixer.app.components.MultiSelectOption
import com.example.rpgaudiomixer.app.components.MultiSelectPickerSheet
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.components.toDisplayDate
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.ui.UiState

@Composable
fun SessionScenesRoute(
    onOpenScene: (Long, Boolean) -> Unit,
    onTitleChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionScenesViewModel = hiltViewModel(),
) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val availableScenes by viewModel.availableScenes.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(session?.name) {
        onTitleChange(session?.name)
    }
    DisposableEffect(Unit) {
        onDispose { onTitleChange(null) }
    }

    SessionScenesScreen(
        session = session,
        uiState = uiState,
        availableScenes = availableScenes,
        errorMessage = errorMessage,
        onOpenScene = onOpenScene,
        onImportScenes = viewModel::importScenes,
        onUnlinkScene = viewModel::unlinkScene,
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@Composable
private fun SessionScenesScreen(
    session: Session?,
    uiState: UiState<List<Scene>>,
    availableScenes: List<Scene>,
    errorMessage: String?,
    onOpenScene: (Long, Boolean) -> Unit,
    onImportScenes: (List<Long>) -> Unit,
    onUnlinkScene: (Long) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showImportSheet by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.CollectionsBookmark,
                    title = "Unable to load session scenes",
                    body = uiState.message,
                    actionLabel = "Dismiss",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                SessionScenesContent(
                    session = session,
                    scenes = uiState.data,
                    onOpenScene = onOpenScene,
                    onImportClick = { showImportSheet = true },
                    onUnlinkScene = onUnlinkScene,
                )
            }
        }

        if (showImportSheet) {
            MultiSelectPickerSheet(
                title = "Import Scene",
                options = availableScenes.map { scene ->
                    MultiSelectOption(
                        id = scene.id,
                        title = scene.name,
                        subtitle = scene.description,
                    )
                },
                onDismiss = { showImportSheet = false },
                onConfirm = { selectedIds ->
                    onImportScenes(selectedIds)
                    showImportSheet = false
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
private fun SessionScenesContent(
    session: Session?,
    scenes: List<Scene>,
    onOpenScene: (Long, Boolean) -> Unit,
    onImportClick: () -> Unit,
    onUnlinkScene: (Long) -> Unit,
) {
    if (scenes.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SessionScenesHeader(session = session)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                EmptyStateView(
                    icon = Icons.Rounded.CollectionsBookmark,
                    title = "No linked scenes",
                    body = "Import scenes from the global library to prepare this session.",
                    actionLabel = "Import Scene",
                    onAction = onImportClick,
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SessionScenesHeader(session = session)
        }
        items(items = scenes, key = { scene -> scene.id }) { scene ->
            SwipeToDeleteContainer(
                onDelete = { onUnlinkScene(scene.id) },
            ) {
                SceneCard(
                    scene = scene,
                    onOpenScene = { onOpenScene(scene.id, false) },
                    onPlayScene = { onOpenScene(scene.id, true) },
                )
            }
        }
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onImportClick,
            ) {
                Text("+ IMPORT SCENE")
            }
        }
    }
}

@Composable
private fun SessionScenesHeader(
    session: Session?,
) {
    if (session == null) {
        return
    }

    Text(
        text = session.dateMillis.toDisplayDate(),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
