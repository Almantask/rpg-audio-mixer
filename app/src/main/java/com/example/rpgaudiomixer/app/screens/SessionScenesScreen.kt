package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumCard
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDim
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurface
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceVariant
import com.example.rpgaudiomixer.app.theme.ArcanumSurface
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.app.viewmodel.SessionScenesViewModel
import com.example.rpgaudiomixer.domain.model.Scene

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScenesScreen(
    onOpenScene: (Long) -> Unit,
    onPlayScene: (Long) -> Unit,
    onBack: () -> Unit,
    onCredits: () -> Unit,
    viewModel: SessionScenesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = state.sessionName.ifBlank { "Session Scenes" },
                onBack = onBack,
                onCredits = onCredits,
            )
        },
        containerColor = ArcanumBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showImportSheet() },
                containerColor = ArcanumGold,
                contentColor = Color(0xFF1A0E00),
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import Scene")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            if (state.scenes.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Layers, null, tint = ArcanumGoldDim, modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("No scenes in this session", color = ArcanumOnSurfaceVariant)
                        }
                    }
                }
            } else {
                items(state.scenes, key = { it.id }) { scene ->
                    SceneSessionCard(
                        scene = scene,
                        onClick = { onOpenScene(scene.id) },
                        onPlay = { onPlayScene(scene.id) },
                        onRemove = { viewModel.removeScene(scene.id) },
                    )
                }
            }
        }
    }

    if (state.showImportSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissImportSheet() },
            sheetState = sheetState,
            containerColor = ArcanumSurface,
        ) {
            ImportScenesSheet(
                availableScenes = state.allScenes,
                existingSceneIds = state.scenes.map { it.id }.toSet(),
                onImport = { viewModel.importScene(it) },
                onCreateNew = { viewModel.showCreateDialog() },
            )
        }
    }

    if (state.showCreateDialog) {
        CreateSceneQuickDialog(
            onDismiss = { viewModel.dismissCreateDialog() },
            onCreate = { name -> viewModel.createAndAddScene(name) },
        )
    }
}

@Composable
private fun SceneSessionCard(
    scene: Scene,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCard)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ArcanumSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Layers, null, tint = ArcanumGoldDim)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(scene.name, fontFamily = FontFamily.Serif, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ArcanumOnSurface)
            if (scene.tags.isNotEmpty()) {
                Text(scene.tags.joinToString(" · "), fontSize = 11.sp, color = ArcanumOnSurfaceVariant)
            }
        }
        IconButton(onClick = onPlay, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Default.PlayArrow, "Play", tint = ArcanumGold)
        }
        IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, "Remove", tint = ArcanumOnSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun ImportScenesSheet(
    availableScenes: List<Scene>,
    existingSceneIds: Set<Long>,
    onImport: (Long) -> Unit,
    onCreateNew: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Import Scene", fontFamily = FontFamily.Serif, fontSize = 20.sp, color = ArcanumGold)
        Spacer(Modifier.height(8.dp))
        val importable = availableScenes.filter { it.id !in existingSceneIds }
        if (importable.isEmpty()) {
            Text("All scenes already added", color = ArcanumOnSurfaceVariant)
        } else {
            importable.forEach { scene ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImport(scene.id) }
                        .background(ArcanumSurfaceVariant)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(scene.name, color = ArcanumOnSurface, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Add, null, tint = ArcanumGold)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onCreateNew,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold, contentColor = Color(0xFF1A0E00)),
        ) {
            Text("CREATE NEW SCENE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun CreateSceneQuickDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumSurface,
        titleContentColor = ArcanumGold,
        title = { Text("New Scene", fontFamily = FontFamily.Serif) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Scene Name", color = ArcanumOnSurfaceVariant) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ArcanumGold,
                    unfocusedBorderColor = ArcanumGoldDim.copy(alpha = 0.4f),
                    focusedTextColor = ArcanumOnSurface,
                    unfocusedTextColor = ArcanumOnSurface,
                ),
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onCreate(name.trim()) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold, contentColor = Color(0xFF1A0E00)),
            ) { Text("CREATE", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = ArcanumOnSurfaceVariant)) { Text("CANCEL") }
        },
    )
}
