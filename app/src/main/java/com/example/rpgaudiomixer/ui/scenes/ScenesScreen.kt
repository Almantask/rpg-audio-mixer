package com.example.rpgaudiomixer.ui.scenes

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
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class ScenesUiState(
    val isLoading: Boolean = true,
    val scenes: List<Scene> = emptyList(),
    val editorState: SceneEditorState? = null,
    val errorMessage: String? = null,
)

data class SceneEditorState(
    val sceneId: Long,
    val name: String,
    val description: String,
    val selectedPredefinedTags: Set<String>,
    val customTagsInput: String,
)

private val predefinedSceneTags = listOf(
    "Tavern",
    "Forest",
    "Combat",
    "City",
    "Dungeon",
    "Ocean",
    "Mountain",
    "Cave",
    "Desert",
    "Magic",
)

@Composable
fun ScenesRoute(
    onOpenScene: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScenesScreen(
        uiState = uiState,
        onCreateScene = viewModel::createScene,
        onStartEditingScene = viewModel::startEditingScene,
        onDismissEditor = viewModel::dismissEditor,
        onUpdateEditorName = viewModel::updateEditorName,
        onUpdateEditorDescription = viewModel::updateEditorDescription,
        onTogglePredefinedTag = viewModel::togglePredefinedTag,
        onUpdateCustomTagsInput = viewModel::updateCustomTagsInput,
        onSaveSceneEdits = viewModel::saveSceneEdits,
        onDeleteScene = viewModel::deleteScene,
        onOpenScene = onOpenScene,
        modifier = modifier,
    )
}

@Composable
fun ScenesScreen(
    uiState: ScenesUiState,
    onCreateScene: (String, String?, String) -> Unit,
    onStartEditingScene: (Scene) -> Unit,
    onDismissEditor: () -> Unit,
    onUpdateEditorName: (String) -> Unit,
    onUpdateEditorDescription: (String) -> Unit,
    onTogglePredefinedTag: (String) -> Unit,
    onUpdateCustomTagsInput: (String) -> Unit,
    onSaveSceneEdits: () -> Unit,
    onDeleteScene: (Long) -> Unit,
    onOpenScene: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable(uiState.errorMessage) { mutableStateOf(uiState.errorMessage) }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.scenes.isEmpty()) {
            EmptyStateView(
                modifier = Modifier.align(Alignment.Center),
                illustration = Icons.Default.EditNote,
                title = "No scenes yet",
                actionLabel = "Add New Scene",
                onActionClick = { showCreateDialog = true },
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                items(items = uiState.scenes, key = Scene::id) { scene ->
                    SwipeToDeleteContainer(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onDelete = { onDeleteScene(scene.id) },
                    ) {
                        SceneCard(
                            scene = scene,
                            onOpenScene = { onOpenScene(scene.id, false) },
                            onPlayScene = { onOpenScene(scene.id, true) },
                            onEditScene = { onStartEditingScene(scene) },
                        )
                    }
                }
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { showCreateDialog = true },
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Text(text = "Add New Scene")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (showCreateDialog) {
            CreateSceneDialog(
                onDismiss = { showCreateDialog = false },
                onCreateScene = { name, description, tags ->
                    onCreateScene(name, description, tags)
                    showCreateDialog = false
                },
            )
        }

        uiState.editorState?.let { editorState ->
            EditSceneDialog(
                editorState = editorState,
                onDismiss = onDismissEditor,
                onUpdateName = onUpdateEditorName,
                onUpdateDescription = onUpdateEditorDescription,
                onTogglePredefinedTag = onTogglePredefinedTag,
                onUpdateCustomTagsInput = onUpdateCustomTagsInput,
                onSave = onSaveSceneEdits,
            )
        }

        ErrorDialog(
            message = errorMessage,
            onDismiss = { errorMessage = null },
        )
    }
}

@Composable
private fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreateScene: (String, String?, String) -> Unit,
) {
    var sceneName by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedPredefinedTags by rememberSaveable { mutableStateOf(setOf<String>()) }
    var customTags by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Create a New Scene") },
        text = {
            Box {
                androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = sceneName,
                        onValueChange = { sceneName = it },
                        label = { Text(text = "Scene name") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(text = "Description") },
                    )
                    Text(text = "Tags", style = MaterialTheme.typography.labelLarge)
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        predefinedSceneTags.forEach { tag ->
                            FilterChip(
                                selected = tag in selectedPredefinedTags,
                                onClick = {
                                    selectedPredefinedTags = if (tag in selectedPredefinedTags) {
                                        selectedPredefinedTags - tag
                                    } else {
                                        selectedPredefinedTags + tag
                                    }
                                },
                                label = { Text(text = tag) },
                            )
                        }
                    }
                    OutlinedTextField(
                        value = customTags,
                        onValueChange = { customTags = it },
                        label = { Text(text = "Custom tags (comma-separated)") },
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val allTags = (
                        selectedPredefinedTags +
                            customTags.split(',').map(String::trim).filter(String::isNotBlank)
                        )
                        .distinct()
                        .joinToString(",")
                    onCreateScene(sceneName, description.ifBlank { null }, allTags)
                },
                enabled = sceneName.isNotBlank(),
            ) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}

@Composable
private fun EditSceneDialog(
    editorState: SceneEditorState,
    onDismiss: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onTogglePredefinedTag: (String) -> Unit,
    onUpdateCustomTagsInput: (String) -> Unit,
    onSave: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit ${editorState.name}") },
        text = {
            androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = editorState.name,
                    onValueChange = onUpdateName,
                    label = { Text(text = "Scene name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = editorState.description,
                    onValueChange = onUpdateDescription,
                    label = { Text(text = "Description") },
                )
                Text(text = "Tags", style = MaterialTheme.typography.labelLarge)
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    predefinedSceneTags.forEach { tag ->
                        FilterChip(
                            selected = tag in editorState.selectedPredefinedTags,
                            onClick = { onTogglePredefinedTag(tag) },
                            label = { Text(text = tag) },
                        )
                    }
                }
                OutlinedTextField(
                    value = editorState.customTagsInput,
                    onValueChange = onUpdateCustomTagsInput,
                    label = { Text(text = "Custom tags (comma-separated)") },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = editorState.name.isNotBlank(),
            ) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
) : ViewModel() {
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    internal constructor(
        sceneRepository: SceneRepository,
        mainDispatcher: CoroutineDispatcher,
    ) : this(sceneRepository) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(ScenesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.observeScenes()
                .catch { throwable ->
                    _uiState.value = ScenesUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load scenes.",
                    )
                }
                .collect { scenes ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        scenes = scenes,
                    )
                }
        }
    }

    fun createScene(
        name: String,
        description: String?,
        tags: String,
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return
        }
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.createScene(
                name = trimmedName,
                description = description?.trim()?.takeIf(String::isNotBlank),
                tags = tags.split(',').map(String::trim).filter(String::isNotBlank),
            )
        }
    }

    fun deleteScene(sceneId: Long) {
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.deleteScene(sceneId)
        }
    }

    fun startEditingScene(scene: Scene) {
        val predefinedMatches = scene.tags.mapNotNull { tag ->
            predefinedSceneTags.firstOrNull { predefinedTag -> predefinedTag.equals(tag, ignoreCase = true) }
        }.toSet()
        val customTags = scene.tags.filterNot { tag ->
            predefinedSceneTags.any { predefinedTag -> predefinedTag.equals(tag, ignoreCase = true) }
        }.joinToString(", ")
        _uiState.value = _uiState.value.copy(
            editorState = SceneEditorState(
                sceneId = scene.id,
                name = scene.name,
                description = scene.description.orEmpty(),
                selectedPredefinedTags = predefinedMatches,
                customTagsInput = customTags,
            )
        )
    }

    fun dismissEditor() {
        _uiState.value = _uiState.value.copy(editorState = null)
    }

    fun updateEditorName(name: String) {
        val editorState = _uiState.value.editorState ?: return
        _uiState.value = _uiState.value.copy(editorState = editorState.copy(name = name))
    }

    fun updateEditorDescription(description: String) {
        val editorState = _uiState.value.editorState ?: return
        _uiState.value = _uiState.value.copy(editorState = editorState.copy(description = description))
    }

    fun togglePredefinedTag(tag: String) {
        val editorState = _uiState.value.editorState ?: return
        val updatedTags = if (tag in editorState.selectedPredefinedTags) {
            editorState.selectedPredefinedTags - tag
        } else {
            editorState.selectedPredefinedTags + tag
        }
        _uiState.value = _uiState.value.copy(editorState = editorState.copy(selectedPredefinedTags = updatedTags))
    }

    fun updateCustomTagsInput(input: String) {
        val editorState = _uiState.value.editorState ?: return
        _uiState.value = _uiState.value.copy(editorState = editorState.copy(customTagsInput = input))
    }

    fun saveSceneEdits() {
        val editorState = _uiState.value.editorState ?: return
        val trimmedName = editorState.name.trim()
        if (trimmedName.isBlank()) {
            return
        }
        val allTags = (
            editorState.selectedPredefinedTags +
                editorState.customTagsInput.split(',').map(String::trim).filter(String::isNotBlank)
            )
            .distinct()
            .sorted()
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.updateScene(
                sceneId = editorState.sceneId,
                name = trimmedName,
                description = editorState.description.trim().ifBlank { null },
                tags = allTags,
            )
            _uiState.value = _uiState.value.copy(editorState = null)
        }
    }
}
