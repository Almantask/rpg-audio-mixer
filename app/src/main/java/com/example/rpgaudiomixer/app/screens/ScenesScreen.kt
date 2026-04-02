package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.DeleteOutline
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
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.rpgaudiomixer.app.theme.ArcanumPurple
import com.example.rpgaudiomixer.app.theme.ArcanumSurface
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.app.viewmodel.ScenesViewModel
import com.example.rpgaudiomixer.domain.model.Scene

private val PRESET_TAGS = listOf(
    "Dungeon", "Forest", "Tavern", "City", "Combat",
    "Exploration", "Mystery", "Horror", "Epic", "Ambient",
)

@Composable
fun ScenesScreen(
    onOpenScene: (Long) -> Unit,
    onPlayScene: (Long) -> Unit,
    onCredits: () -> Unit,
    viewModel: ScenesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { ArcanumTopBar(title = "SCENES", onCredits = onCredits) },
        containerColor = ArcanumBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                containerColor = ArcanumGold,
                contentColor = Color(0xFF1A0E00),
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Scene")
            }
        },
    ) { innerPadding ->
        if (state.scenes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Layers, null, tint = ArcanumGoldDim, modifier = Modifier.size(72.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("No scenes yet", fontFamily = FontFamily.Serif, fontSize = 20.sp, color = ArcanumOnSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap + to create your first scene", fontSize = 13.sp, color = ArcanumOnSurfaceVariant.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                items(state.scenes, key = { it.id }) { scene ->
                    SceneListCard(
                        scene = scene,
                        onClick = { onOpenScene(scene.id) },
                        onPlay = { onPlayScene(scene.id) },
                        onDelete = { viewModel.deleteScene(scene.id) },
                    )
                }
            }
        }
    }

    if (state.showCreateDialog) {
        CreateSceneDialog(
            onDismiss = { viewModel.dismissCreateDialog() },
            onCreate = { name, tags -> viewModel.createScene(name, tags) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SceneListCard(
    scene: Scene,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCard)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArcanumPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Layers, null, tint = ArcanumPurple, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scene.name,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArcanumOnSurface,
                )
            }
            IconButton(onClick = onPlay, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.PlayArrow, "Play", tint = ArcanumGold, modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, "Delete", tint = ArcanumOnSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }
        if (scene.tags.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                scene.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ArcanumGoldDim.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(tag, fontSize = 10.sp, color = ArcanumGoldDim, letterSpacing = 0.5.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreate: (String, List<String>) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var customTagInput by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumSurface,
        titleContentColor = ArcanumGold,
        textContentColor = ArcanumOnSurface,
        title = { Text("New Scene", fontFamily = FontFamily.Serif) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                Text("Tags", fontSize = 12.sp, color = ArcanumOnSurfaceVariant, letterSpacing = 1.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    PRESET_TAGS.forEach { tag ->
                        val selected = tag in selectedTags
                        InputChip(
                            selected = selected,
                            onClick = { if (selected) selectedTags.remove(tag) else selectedTags.add(tag) },
                            label = { Text(tag, fontSize = 11.sp) },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = ArcanumGold.copy(alpha = 0.2f),
                                selectedLabelColor = ArcanumGold,
                                containerColor = ArcanumSurfaceVariant,
                                labelColor = ArcanumOnSurfaceVariant,
                            ),
                            border = InputChipDefaults.inputChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = ArcanumGold.copy(alpha = 0.5f),
                                borderColor = ArcanumGoldDim.copy(alpha = 0.2f),
                                borderWidth = 1.dp,
                                selectedBorderWidth = 1.dp,
                            ),
                        )
                    }
                }
                // Custom tag input
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = customTagInput,
                        onValueChange = { customTagInput = it },
                        label = { Text("Custom tag", color = ArcanumOnSurfaceVariant, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArcanumGold,
                            unfocusedBorderColor = ArcanumGoldDim.copy(alpha = 0.3f),
                            focusedTextColor = ArcanumOnSurface,
                            unfocusedTextColor = ArcanumOnSurface,
                        ),
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            val tag = customTagInput.trim()
                            if (tag.isNotBlank() && tag !in selectedTags) {
                                selectedTags.add(tag)
                                customTagInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Add, "Add tag", tint = ArcanumGold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onCreate(name.trim(), selectedTags.toList()) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold, contentColor = Color(0xFF1A0E00)),
            ) {
                Text("CREATE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = ArcanumOnSurfaceVariant)) {
                Text("CANCEL")
            }
        },
    )
}
