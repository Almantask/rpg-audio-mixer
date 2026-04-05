package com.example.rpgaudiomixer.app.screens.campaigns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.MultiSelectPickerSheet
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun SessionScenesScreen(
    sessionId: Long,
    viewModel: SessionScenesViewModel = hiltViewModel(),
    onOpenScene: (Long, Boolean) -> Unit
) {
    LaunchedEffect(sessionId) {
        viewModel.setSessionId(sessionId)
    }

    val sessionScenes by viewModel.sessionScenes.collectAsState()
    val allGlobalScenes by viewModel.allGlobalScenes.collectAsState()
    var showImportSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BlackBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showImportSheet = true },
                containerColor = Gold,
                contentColor = BlackBg,
                shape = Shapes.medium
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import Scene")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (sessionScenes.isEmpty()) {
                EmptyStateView(
                    illustration = Icons.Default.Landscape,
                    message = "NO SCENES LINKED",
                    ctaText = "IMPORT SCENE",
                    onCtaClick = { showImportSheet = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sessionScenes, key = { it.id }) { scene ->
                        SwipeToDeleteContainer(
                            onDelete = { viewModel.unlinkScene(scene.id) }
                        ) {
                            SceneCard(
                                scene = scene,
                                onPlay = { onOpenScene(it, true) },
                                onClick = { onOpenScene(it, false) }
                            )
                        }
                    }
                }
            }
        }

        if (showImportSheet) {
            MultiSelectPickerSheet(
                title = "IMPORT SCENE",
                items = allGlobalScenes,
                selectedItems = sessionScenes.map { it.id }.toSet(),
                onDismiss = { showImportSheet = false },
                onItemSelected = { sceneId ->
                    viewModel.linkScene(sceneId)
                },
                itemLabel = { it.name },
                itemId = { it.id }
            )
        }
    }
}
