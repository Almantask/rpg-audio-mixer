package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.MultiSelectOption
import com.example.rpgaudiomixer.app.components.MultiSelectPickerSheet
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.components.toDisplayDate
import com.example.rpgaudiomixer.app.navigation.AppRoute
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class SessionScenesUiState(
    val isLoading: Boolean = true,
    val session: Session? = null,
    val linkedScenes: List<Scene> = emptyList(),
    val availableScenes: List<Scene> = emptyList(),
    val isImportPickerVisible: Boolean = false,
    val errorMessage: String? = null,
)

@Composable
fun SessionScenesRoute(
    onOpenScene: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SessionScenesScreen(
        uiState = uiState,
        onShowImportPicker = viewModel::showImportPicker,
        onDismissImportPicker = viewModel::dismissImportPicker,
        onImportScenes = viewModel::importScenes,
        onUnlinkScene = viewModel::unlinkScene,
        onOpenScene = onOpenScene,
        modifier = modifier,
    )
}

@Composable
fun SessionScenesScreen(
    uiState: SessionScenesUiState,
    onShowImportPicker: () -> Unit,
    onDismissImportPicker: () -> Unit,
    onImportScenes: (List<Long>) -> Unit,
    onUnlinkScene: (Long) -> Unit,
    onOpenScene: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var errorMessage by remember(uiState.errorMessage) { mutableStateOf(uiState.errorMessage) }
    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.linkedScenes.isEmpty()) {
            EmptyStateView(
                modifier = Modifier.align(Alignment.Center),
                illustration = Icons.Default.AutoStories,
                title = "No scenes linked yet",
                actionLabel = "Import Scene",
                onActionClick = onShowImportPicker,
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        text = uiState.session?.date?.toDisplayDate() ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(items = uiState.linkedScenes, key = Scene::id) { scene ->
                    SwipeToDeleteContainer(
                        modifier = Modifier.padding(horizontal = 16.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = onShowImportPicker,
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Text(text = "Import Scene")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (uiState.isImportPickerVisible) {
            val selectedIds = remember { mutableStateListOf<Long>() }
            MultiSelectPickerSheet(
                title = "Import Scene",
                options = uiState.availableScenes.map { scene ->
                    MultiSelectOption(id = scene.id, label = scene.name)
                },
                selectedIds = selectedIds.toSet(),
                onToggle = { sceneId ->
                    if (selectedIds.contains(sceneId)) {
                        selectedIds.remove(sceneId)
                    } else {
                        selectedIds += sceneId
                    }
                },
                onDismiss = onDismissImportPicker,
                onConfirm = { onImportScenes(selectedIds.toList()) },
            )
        }

        ErrorDialog(
            message = errorMessage,
            onDismiss = { errorMessage = null },
        )
    }
}

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
) : ViewModel() {
    private val sessionId: Long = requireNotNull(savedStateHandle[AppRoute.SESSION_ID_ARG])
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    internal constructor(
        sessionId: Long,
        sessionRepository: SessionRepository,
        sceneRepository: SceneRepository,
        mainDispatcher: CoroutineDispatcher,
    ) : this(
        savedStateHandle = SavedStateHandle(mapOf(AppRoute.SESSION_ID_ARG to sessionId)),
        sessionRepository = sessionRepository,
        sceneRepository = sceneRepository,
    ) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(SessionScenesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            combine(
                sessionRepository.observeSession(sessionId),
                sceneRepository.observeScenesForSession(sessionId),
                sceneRepository.observeAvailableScenesForSession(sessionId),
            ) { session, linkedScenes, availableScenes ->
                SessionScenesUiState(
                    isLoading = false,
                    session = session,
                    linkedScenes = linkedScenes,
                    availableScenes = availableScenes,
                    isImportPickerVisible = _uiState.value.isImportPickerVisible,
                )
            }
                .catch { throwable ->
                    _uiState.value = SessionScenesUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load session scenes.",
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun showImportPicker() {
        _uiState.value = _uiState.value.copy(isImportPickerVisible = true)
    }

    fun dismissImportPicker() {
        _uiState.value = _uiState.value.copy(isImportPickerVisible = false)
    }

    fun importScenes(sceneIds: List<Long>) {
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.linkScenesToSession(sessionId = sessionId, sceneIds = sceneIds)
            _uiState.value = _uiState.value.copy(isImportPickerVisible = false)
        }
    }

    fun unlinkScene(sceneId: Long) {
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.unlinkSceneFromSession(sessionId = sessionId, sceneId = sceneId)
        }
    }
}
