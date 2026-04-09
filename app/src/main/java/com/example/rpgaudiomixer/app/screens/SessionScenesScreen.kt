package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.ui.sessions.SessionScenesViewModel
import com.example.rpgaudiomixer.app.ui.sessions.SessionScenesUiState
import com.example.rpgaudiomixer.domain.scene.Scene

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScenesScreen(
    viewModel: SessionScenesViewModel = hiltViewModel(),
    onSceneClick: (Long, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showImportSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Scenes", color = ArcanumGold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ArcanumGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArcanumBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showImportSheet = true },
                containerColor = ArcanumGold,
                contentColor = ArcanumOnGold
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import Scene")
            }
        },
        containerColor = ArcanumBlack
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is SessionScenesUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ArcanumGold
                    )
                }
                is SessionScenesUiState.Success -> {
                    if (state.linkedScenes.isEmpty()) {
                        EmptyStateView(
                            illustration = Icons.Default.Image,
                            title = "No scenes linked",
                            subtitle = "Link global scenes to this session for quick access.",
                            actionLabel = "IMPORT SCENE",
                            onAction = { showImportSheet = true }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.linkedScenes, key = { it.id }) { scene ->
                                SwipeToDeleteContainer(
                                    onDelete = { viewModel.unlinkScene(scene.id) }
                                ) {
                                    SceneCard(
                                        scene = scene,
                                        onPlayClick = { onSceneClick(scene.id) },
                                        onCardClick = { onSceneClick(scene.id) }
                                    )
                                }
                            }
                        }
                    }

                    if (showImportSheet) {
                        MultiSelectPickerSheet(
                            allAvailableItems = state.allAvailableScenes,
                            alreadySelectedIds = state.linkedScenes.map { it.id }.toSet(),
                            onDismiss = { showImportSheet = false },
                            onItemSelected = { viewModel.linkScene(it) }
                        )
                    }
                }
                is SessionScenesUiState.Error -> {
                    Text(text = state.message, color = ArcanumErrorRed)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectPickerSheet(
    allAvailableItems: List<Scene>,
    alreadySelectedIds: Set<Long>,
    onDismiss: () -> Unit,
    onItemSelected: (Long) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ArcanumCard,
        contentColor = ArcanumOnSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "IMPORT SCENES",
                style = MaterialTheme.typography.titleMedium,
                color = ArcanumGold,
                modifier = Modifier.padding(16.dp)
            )

            if (allAvailableItems.isEmpty()) {
                Text(
                    text = "No scenes to import. Create one in the Scenes tab first.",
                    modifier = Modifier.padding(16.dp),
                    color = ArcanumOnSurface.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn {
                    items(allAvailableItems) { item ->
                        val isSelected = alreadySelectedIds.contains(item.id)
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            headlineContent = { Text(item.name, color = if (isSelected) ArcanumMutedGold else ArcanumGold) },
                            supportingContent = { Text(item.tags.joinToString(", "), color = ArcanumOnSurface.copy(alpha = 0.4f)) },
                            trailingContent = {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Linked", tint = ArcanumMutedGold)
                                } else {
                                    IconButton(onClick = { onItemSelected(item.id) }) {
                                        Icon(Icons.Default.Add, contentDescription = "Link", tint = ArcanumGold)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
