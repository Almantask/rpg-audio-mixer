package com.example.rpgaudiomixer.app.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.rpgaudiomixer.app.theme.ArcanumPink
import com.example.rpgaudiomixer.app.theme.ArcanumPurple
import com.example.rpgaudiomixer.app.theme.ArcanumSurface
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.app.viewmodel.LibraryViewModel
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

@Composable
fun LibraryScreen(
    onEditCategory: (Long) -> Unit,
    onNewCategory: () -> Unit,
    onBack: () -> Unit,
    onCredits: () -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "ARCANUM LIBRARY",
                onBack = onBack,
                onCredits = onCredits,
            )
        },
        containerColor = ArcanumBlack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = ArcanumSurface,
                contentColor = ArcanumGold,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab),
                        color = ArcanumGold,
                    )
                },
            ) {
                listOf("SOUNDSCAPES", "SOUND EFFECTS").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontSize = 11.sp,
                                letterSpacing = 0.8.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        selectedContentColor = ArcanumGold,
                        unselectedContentColor = ArcanumOnSurfaceVariant,
                    )
                }
            }

            when (selectedTab) {
                0 -> SoundscapesLibraryTab(
                    categories = state.categories,
                    onEditCategory = onEditCategory,
                    onDeleteCategory = viewModel::deleteCategory,
                    onNewCategory = {
                        showCreateCategoryDialog = true
                    },
                )
                1 -> FXLibraryTab(
                    fxTracks = state.fxTracks,
                    editingFx = state.editingFxTrack,
                    onImport = { path, name -> viewModel.importFXTrack(name, path) },
                    onEditFx = viewModel::openEditFX,
                    onSaveFx = viewModel::saveEditFX,
                    onCloseEdit = viewModel::closeEditFX,
                    onDeleteFx = viewModel::deleteFXTrack,
                )
            }
        }
    }

    if (showCreateCategoryDialog) {
        CreateCategoryDialog(
            onConfirm = { name ->
                viewModel.createCategory(name)
                showCreateCategoryDialog = false
            },
            onDismiss = { showCreateCategoryDialog = false },
        )
    }
}

@Composable
private fun SoundscapesLibraryTab(
    categories: List<SoundscapeCategory>,
    onEditCategory: (Long) -> Unit,
    onDeleteCategory: (Long) -> Unit,
    onNewCategory: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
    ) {
        if (categories.isEmpty()) {
            item {
                EmptyLibraryState(
                    message = "No soundscape categories yet.\nCreate one to start composing.",
                )
            }
        }

        items(categories, key = { it.id }) { category ->
            SoundscapeCategoryRow(
                category = category,
                onEdit = { onEditCategory(category.id) },
                onDelete = { onDeleteCategory(category.id) },
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            AddLibraryButton("NEW CATEGORY", onNewCategory)
        }
    }
}

@Composable
private fun SoundscapeCategoryRow(
    category: SoundscapeCategory,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ArcanumCard)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .clickable(onClick = onEdit)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.MusicNote, null, tint = ArcanumPurple, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.name,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ArcanumOnSurface,
            )
            val trackCounts = IntensityLevel.entries.mapNotNull { level ->
                val count = category.tracksFor(level).size
                if (count > 0) "${level.label}: $count" else null
            }.joinToString("  ·  ")
            if (trackCounts.isNotEmpty()) {
                Text(
                    text = trackCounts,
                    fontSize = 11.sp,
                    color = ArcanumOnSurfaceVariant,
                    letterSpacing = 0.5.sp,
                )
            } else {
                Text("No tracks", fontSize = 11.sp, color = ArcanumOnSurfaceVariant.copy(alpha = 0.5f))
            }
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, "Edit", tint = ArcanumGoldDim, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, "Delete", tint = ArcanumOnSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun FXLibraryTab(
    fxTracks: List<FXTrack>,
    editingFx: FXTrack?,
    onImport: (String, String) -> Unit,
    onEditFx: (FXTrack) -> Unit,
    onSaveFx: (FXTrack) -> Unit,
    onCloseEdit: () -> Unit,
    onDeleteFx: (Long) -> Unit,
) {
    val context = LocalContext.current
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
            val name = it.lastPathSegment?.substringAfterLast("/")
                ?.substringBeforeLast(".") ?: "FX Track"
            onImport(it.toString(), name)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
    ) {
        if (fxTracks.isEmpty()) {
            item {
                EmptyLibraryState(
                    message = "No sound effects yet.\nImport audio files to build your soundboard.",
                )
            }
        }

        items(fxTracks, key = { it.id }) { fxTrack ->
            FXTrackRow(
                fxTrack = fxTrack,
                onEdit = { onEditFx(fxTrack) },
                onDelete = { onDeleteFx(fxTrack.id) },
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            AddLibraryButton("IMPORT SOUND EFFECT") {
                filePicker.launch(arrayOf("audio/*"))
            }
        }
    }

    if (editingFx != null) {
        EditFXDialog(
            fxTrack = editingFx,
            onSave = onSaveFx,
            onDismiss = onCloseEdit,
        )
    }
}

@Composable
private fun FXTrackRow(
    fxTrack: FXTrack,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ArcanumCard)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.AudioFile, null, tint = ArcanumPink, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fxTrack.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = ArcanumOnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (fxTrack.tags.isNotEmpty()) {
                Text(
                    text = fxTrack.tags.joinToString(", "),
                    fontSize = 11.sp,
                    color = ArcanumOnSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, "Edit", tint = ArcanumGoldDim, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, "Delete", tint = ArcanumOnSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun EditFXDialog(
    fxTrack: FXTrack,
    onSave: (FXTrack) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember(fxTrack.id) { mutableStateOf(fxTrack.name) }
    var tagsText by remember(fxTrack.id) { mutableStateOf(fxTrack.tags.joinToString(", ")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumSurface,
        title = {
            Text("EDIT EFFECT", fontFamily = FontFamily.Serif, color = ArcanumGold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ArcanumTextField(label = "Name", value = name, onValueChange = { name = it })
                ArcanumTextField(
                    label = "Tags (comma separated)",
                    value = tagsText,
                    onValueChange = { tagsText = it },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val tags = tagsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                onSave(fxTrack.copy(name = name.trim(), tags = tags))
            }) {
                Text("SAVE", color = ArcanumGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = ArcanumOnSurfaceVariant)
            }
        },
    )
}

@Composable
private fun CreateCategoryDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumSurface,
        title = {
            Text("NEW SOUNDSCAPE CATEGORY", fontFamily = FontFamily.Serif, color = ArcanumGold, fontSize = 16.sp)
        },
        text = {
            ArcanumTextField("Category Name", name, { name = it })
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                enabled = name.isNotBlank(),
            ) {
                Text("CREATE", color = if (name.isNotBlank()) ArcanumGold else ArcanumOnSurfaceVariant, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = ArcanumOnSurfaceVariant) }
        },
    )
}

@Composable
private fun EmptyLibraryState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Default.MusicNote,
            null,
            tint = ArcanumGoldDim.copy(alpha = 0.4f),
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = ArcanumOnSurfaceVariant.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 20.sp,
        )
    }
}

@Composable
private fun AddLibraryButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = ArcanumGold.copy(alpha = 0.12f),
            contentColor = ArcanumGold,
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArcanumGoldDim.copy(alpha = 0.35f)),
    ) {
        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
internal fun ArcanumTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ArcanumGold,
            unfocusedBorderColor = ArcanumGoldDim.copy(alpha = 0.4f),
            focusedLabelColor = ArcanumGold,
            unfocusedLabelColor = ArcanumOnSurfaceVariant,
            cursorColor = ArcanumGold,
            focusedTextColor = ArcanumOnSurface,
            unfocusedTextColor = ArcanumOnSurface,
        ),
    )
}
