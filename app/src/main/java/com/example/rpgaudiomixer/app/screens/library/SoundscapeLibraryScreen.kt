package com.example.rpgaudiomixer.app.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.BentoCategoryCard
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun SoundscapeLibraryScreen(
    viewModel: SoundscapeLibraryViewModel = hiltViewModel(),
    onEditCategory: (Long) -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (categories.isEmpty()) {
            EmptyStateView(
                illustration = Icons.Default.AutoAwesome,
                message = "THE ARCHIVES ARE SILENT",
                ctaText = "NEW COMPOSITION",
                onCtaClick = { showCreateDialog = true }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    SwipeToDeleteContainer(
                        onDelete = { viewModel.deleteCategory(category.id) }
                    ) {
                        BentoCategoryCard(
                            category = category,
                            onEdit = { onEditCategory(it) }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Gold,
            contentColor = BlackBg,
            shape = Shapes.medium
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Composition")
        }

        if (showCreateDialog) {
            CategoryCreateDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name ->
                    viewModel.createCategory(name)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCreateDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(Shapes.large)
            .background(CardSurface)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NEW COMPOSITION",
                style = Typography.headlineMedium,
                color = Gold,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("CATEGORY NAME", color = Gold.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors(),
                singleLine = true
            )

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
                            onCreate(name)
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
