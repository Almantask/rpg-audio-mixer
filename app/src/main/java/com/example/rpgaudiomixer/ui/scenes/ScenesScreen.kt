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
    val errorMessage: String? = null,
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
        onDeleteScene = viewModel::deleteScene,
        onOpenScene = onOpenScene,
        modifier = modifier,
    )
}

@Composable
fun ScenesScreen(
    uiState: ScenesUiState,
    onCreateScene: (String, String?, String) -> Unit,
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
                illustration = Icons.Default.AutoStories,
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
    var tags by rememberSaveable { mutableStateOf("") }

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
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text(text = "Tags (comma-separated)") },
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreateScene(sceneName, description.ifBlank { null }, tags) },
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
                    _uiState.value = ScenesUiState(
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
}
