package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.screens.MainScreenTestTags
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.Scene

object ScenesTestTags {
    const val LIST = "Scenes_List"
    const val EMPTY_ILLUSTRATION = "Campaigns_Empty_Illustration"
    const val NEW_BUTTON = "Scenes_New_Button"
    const val CREATE_DIALOG = "Scenes_Create_Dialog"
    const val NAME_INPUT = "Scenes_Create_Name"
    const val ACTIVE_SCENE = "Scenes_Active_Scene"

    fun card(name: String): String = "Scenes_Card_${name.asTagSuffix()}"
    fun playButton(name: String): String = "Scenes_Play_${name.asTagSuffix()}"
}

@Composable
fun ScenesRoute(
    onOpenScene: (Long, Boolean) -> Unit,
    viewModel: ScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    ScenesScreen(
        uiState = uiState,
        onOpenCreateDialog = viewModel::openCreateDialog,
        onDraftNameChange = viewModel::updateDraftName,
        onConfirmCreate = viewModel::confirmCreateScene,
        onDismissCreate = viewModel::dismissCreateDialog,
        onDeleteScene = viewModel::deleteScene,
        onOpenScene = { scene -> onOpenScene(scene.id, false) },
        onPlayScene = { scene -> onOpenScene(scene.id, true) },
        onDismissError = viewModel::clearError,
    )
}

@Composable
fun ScenesScreen(
    uiState: ScenesUiState,
    onOpenCreateDialog: () -> Unit,
    onDraftNameChange: (String) -> Unit,
    onConfirmCreate: () -> Unit,
    onDismissCreate: () -> Unit,
    onDeleteScene: (Scene) -> Unit,
    onOpenScene: (Scene) -> Unit,
    onPlayScene: (Scene) -> Unit,
    onDismissError: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(MainScreenTestTags.SCENES),
    ) {
        if (uiState.scenes.isEmpty()) {
            ScenesEmptyState(onCreateScene = onOpenCreateDialog)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag(ScenesTestTags.LIST),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.scenes, key = Scene::id) { scene ->
                    SwipeToDeleteSceneContainer(scene = scene, onDeleteScene = onDeleteScene) {
                        SceneCard(
                            scene = scene,
                            modifier = Modifier.testTag(ScenesTestTags.card(scene.name)),
                            playButtonTag = ScenesTestTags.playButton(scene.name),
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
                .testTag(ScenesTestTags.NEW_BUTTON),
            onClick = onOpenCreateDialog,
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("Add New Scene")
        }

        if (uiState.showCreateDialog) {
            AlertDialog(
                modifier = Modifier.testTag(ScenesTestTags.CREATE_DIALOG),
                onDismissRequest = onDismissCreate,
                title = { Text("Add New Scene") },
                text = {
                    OutlinedTextField(
                        modifier = Modifier.testTag(ScenesTestTags.NAME_INPUT),
                        value = uiState.draftName,
                        onValueChange = onDraftNameChange,
                        label = { Text("Scene name") },
                        singleLine = true,
                    )
                },
                confirmButton = {
                    TextButton(onClick = onConfirmCreate) { Text("Create") }
                },
                dismissButton = {
                    TextButton(onClick = onDismissCreate) { Text("Cancel") }
                },
            )
        }

        ErrorDialog(message = uiState.errorMessage, onDismiss = onDismissError)
    }
}

@Composable
private fun ScenesEmptyState(onCreateScene: () -> Unit) {
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
                .testTag(ScenesTestTags.EMPTY_ILLUSTRATION),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                tint = ArcanumGold,
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "No scenes yet",
            fontWeight = FontWeight.Bold,
        )
        FilledTonalButton(modifier = Modifier.padding(top = 20.dp), onClick = onCreateScene) {
            Text("Add New Scene")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteSceneContainer(
    scene: Scene,
    onDeleteScene: (Scene) -> Unit,
    content: @Composable () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteScene(scene)
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
                Text("Move to Trash", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        content = content,
    )
}

private fun String.asTagSuffix(): String = lowercase(java.util.Locale.US)
    .replace(Regex("[^a-z0-9]+"), "_")
    .trim('_')
