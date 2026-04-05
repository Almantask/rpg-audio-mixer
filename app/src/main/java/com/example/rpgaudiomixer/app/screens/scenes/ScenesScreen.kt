package com.example.rpgaudiomixer.app.screens.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun ScenesScreen(
    viewModel: ScenesViewModel = hiltViewModel(),
    onOpenScene: (Long, Boolean) -> Unit
) {
    val scenes by viewModel.scenes.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BlackBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Gold,
                contentColor = BlackBg,
                shape = Shapes.medium
            ) {
                Icon(Icons.Default.Add, contentDescription = "Scribe New Scene")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (scenes.isEmpty()) {
                EmptyStateView(
                    illustration = Icons.Default.Landscape,
                    message = "THE WORLD IS EMPTY",
                    ctaText = "ADD NEW SCENE",
                    onCtaClick = { showCreateDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(scenes, key = { it.id }) { scene ->
                        SwipeToDeleteContainer(
                            onDelete = { viewModel.deleteScene(scene.id) }
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

        if (showCreateDialog) {
            SceneCreateDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, desc, tags ->
                    viewModel.createScene(name, desc, tags)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneCreateDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tagInput by remember { mutableStateOf("") }
    
    val predefinedTags = listOf("Tavern", "Forest", "Combat", "City", "Dungeon", "Ocean", "Mountain", "Cave", "Desert", "Magic")
    val selectedTags = remember { mutableStateListOf<String>() }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(Shapes.large)
            .background(CardSurface)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "CRAFT NEW SCENE",
                style = Typography.headlineMedium,
                color = Gold,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("SCENE NAME", color = Gold.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("DESCRIPTION (OPTIONAL)", color = Gold.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TAGS",
                style = Typography.labelLarge,
                color = Gold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                predefinedTags.forEach { tag ->
                    FilterChip(
                        selected = selectedTags.contains(tag),
                        onClick = {
                            if (selectedTags.contains(tag)) selectedTags.remove(tag)
                            else selectedTags.add(tag)
                        },
                        label = { Text(tag.uppercase()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Gold,
                            selectedLabelColor = BlackBg,
                            labelColor = Gold,
                            containerColor = Color.Transparent
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedTags.contains(tag),
                            borderColor = Gold.copy(alpha = 0.3f),
                            selectedBorderColor = Gold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Gold
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCEL")
                }
                
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onCreate(name, description.ifBlank { null }, selectedTags.toList())
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = BlackBg
                    ),
                    enabled = name.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    shape = Shapes.medium
                ) {
                    Text("CREATE", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Gold,
    unfocusedTextColor = Gold,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Gold,
    unfocusedIndicatorColor = Gold.copy(alpha = 0.3f),
    cursorColor = Gold
)
