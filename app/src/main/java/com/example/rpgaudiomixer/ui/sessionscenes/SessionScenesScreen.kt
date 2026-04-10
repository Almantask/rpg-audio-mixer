package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.ui.scenes.SceneCard
import com.example.rpgaudiomixer.ui.scenes.ScenesTestTags
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SessionScenesTestTags {
    const val SCREEN = "Screen_SessionScenes"
    const val EMPTY_ILLUSTRATION = ScenesTestTags.EMPTY_ILLUSTRATION
    const val IMPORT_BUTTON = "SessionScenes_Import_Button"
    const val IMPORT_DIALOG = "SessionScenes_Import_Dialog"

    fun card(name: String): String = "SessionScenes_Card_${name.asTagSuffix()}"
    fun pickerOption(name: String): String = "SessionScenes_Picker_${name.asTagSuffix()}"
    fun playButton(name: String): String = "SessionScenes_Play_${name.asTagSuffix()}"
}

data class SessionScenesUiState(
    val isLoading: Boolean = true,
    val session: Session? = null,
    val linkedScenes: List<Scene> = emptyList(),
    val availableScenes: List<Scene> = emptyList(),
    val selectedSceneIds: Set<Long> = emptySet(),
    val showImportDialog: Boolean = false,
)

@Composable
fun SessionScenesRoute(
    onOpenScene: (Long, Boolean, Long?) -> Unit,
    viewModel: SessionScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    SessionScenesScreen(
        uiState = uiState,
        onOpenImportDialog = viewModel::openImportDialog,
        onDismissImportDialog = viewModel::dismissImportDialog,
        onToggleScene = viewModel::toggleSceneSelection,
        onConfirmImport = viewModel::confirmImport,
        onUnlinkScene = viewModel::unlinkScene,
        onOpenScene = { scene -> onOpenScene(scene.id, false, uiState.session?.id) },
        onPlayScene = { scene -> onOpenScene(scene.id, true, uiState.session?.id) },
    )
}

@Composable
fun SessionScenesScreen(
    uiState: SessionScenesUiState,
    onOpenImportDialog: () -> Unit,
    onDismissImportDialog: () -> Unit,
    onToggleScene: (Long) -> Unit,
    onConfirmImport: () -> Unit,
    onUnlinkScene: (Scene) -> Unit,
    onOpenScene: (Scene) -> Unit,
    onPlayScene: (Scene) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SessionScenesTestTags.SCREEN),
    ) {
        if (uiState.linkedScenes.isEmpty()) {
            SessionScenesEmptyState(onImportScene = onOpenImportDialog)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = uiState.session?.name.orEmpty(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(text = uiState.session?.dateMillis?.toReadableDate().orEmpty())
                    }
                }
                items(uiState.linkedScenes, key = Scene::id) { scene ->
                    SwipeToUnlinkSceneContainer(scene = scene, onUnlinkScene = onUnlinkScene) {
                        SceneCard(
                            scene = scene,
                            modifier = Modifier.testTag(SessionScenesTestTags.card(scene.name)),
                            playButtonTag = SessionScenesTestTags.playButton(scene.name),
                            onOpenScene = onOpenScene,
                            onPlayScene = onPlayScene,
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding()
                .testTag(SessionScenesTestTags.IMPORT_BUTTON),
            onClick = onOpenImportDialog,
        ) {
            Icon(Icons.Default.LibraryAdd, contentDescription = null)
            Text("Import Scene")
        }

        if (uiState.showImportDialog) {
            AlertDialog(
                modifier = Modifier.testTag(SessionScenesTestTags.IMPORT_DIALOG),
                onDismissRequest = onDismissImportDialog,
                title = { Text("Import Scene") },
                text = {
                    if (uiState.availableScenes.isEmpty()) {
                        Text("No more global scenes to import.")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            uiState.availableScenes.forEach { scene ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onToggleScene(scene.id) }
                                        .padding(vertical = 4.dp)
                                        .testTag(SessionScenesTestTags.pickerOption(scene.name)),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Checkbox(
                                        checked = scene.id in uiState.selectedSceneIds,
                                        onCheckedChange = { onToggleScene(scene.id) },
                                    )
                                    Text(scene.name)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onConfirmImport) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = onDismissImportDialog) { Text("Cancel") }
                },
            )
        }
    }
}

@Composable
private fun SessionScenesEmptyState(
    onImportScene: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(28.dp),
                )
                .testTag(SessionScenesTestTags.EMPTY_ILLUSTRATION),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.MovieFilter,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "No linked scenes yet",
            fontWeight = FontWeight.Bold,
        )
        FilledTonalButton(modifier = Modifier.padding(top = 20.dp), onClick = onImportScene) {
            Text("Import Scene")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToUnlinkSceneContainer(
    scene: Scene,
    onUnlinkScene: (Scene) -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onUnlinkScene(scene)
                true
            } else {
                false
            }
        },
    )
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text("Unlink Scene", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        content = content,
    )
}

@HiltViewModel
class SessionScenesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    sceneRepository: SceneRepository,
) : ViewModel() {
    private val sessionId = requireNotNull(savedStateHandle.get<String>("sessionId")) {
        "Navigation argument 'sessionId' is missing."
    }.toLongOrNull() ?: error("Navigation argument 'sessionId' must be a valid numeric value.")

    private val pickerState = MutableStateFlow(ScenePickerState())
    private val _uiState = MutableStateFlow(SessionScenesUiState())
    val uiState: StateFlow<SessionScenesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                sessionRepository.observeSession(sessionId),
                sessionRepository.observeScenesForSession(sessionId),
                sceneRepository.observeScenes(),
                pickerState,
            ) { session, linkedScenes, allScenes, picker ->
                SessionScenesUiState(
                    isLoading = false,
                    session = session,
                    linkedScenes = linkedScenes,
                    availableScenes = allScenes.filter { candidate ->
                        linkedScenes.none { linked -> linked.id == candidate.id }
                    },
                    selectedSceneIds = picker.selectedSceneIds,
                    showImportDialog = picker.isOpen,
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun openImportDialog() {
        pickerState.value = ScenePickerState(isOpen = true)
    }

    fun dismissImportDialog() {
        pickerState.value = ScenePickerState()
    }

    fun toggleSceneSelection(sceneId: Long) {
        pickerState.update { state ->
            val updated = state.selectedSceneIds.toMutableSet()
            if (!updated.add(sceneId)) {
                updated.remove(sceneId)
            }
            state.copy(selectedSceneIds = updated)
        }
    }

    fun confirmImport() {
        val sceneIds = pickerState.value.selectedSceneIds.toList()
        viewModelScope.launch {
            sessionRepository.linkScenes(sessionId, sceneIds)
            dismissImportDialog()
        }
    }

    fun unlinkScene(scene: Scene) {
        viewModelScope.launch {
            sessionRepository.unlinkScene(sessionId, scene.id)
        }
    }
}

private data class ScenePickerState(
    val isOpen: Boolean = false,
    val selectedSceneIds: Set<Long> = emptySet(),
)

private val sessionScenesDateFormatter = ThreadLocal.withInitial {
    SimpleDateFormat("MMM d, yyyy", Locale.US)
}

private fun Long.toReadableDate(): String = sessionScenesDateFormatter.get().format(Date(this))

private fun String.asTagSuffix(): String = lowercase(Locale.US)
    .replace(Regex("[^a-z0-9]+"), "_")
    .trim('_')
