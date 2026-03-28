package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.theme.ArcanumBorder
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.app.theme.ArcanumGrayMid
import com.example.rpgaudiomixer.domain.model.FxEffect
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.EmptyState
import com.example.rpgaudiomixer.ui.components.PrimaryButton

private val tabs = listOf("SOUNDSCAPES", "SOUND EFFECTS")

@Composable
fun LibraryScreen(
    onOpenComposer: (Long) -> Unit,
    onCredits: () -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showImportFxDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(onCredits = onCredits)

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = ArcanumGold,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(2.dp)
                        .background(ArcanumGold),
                )
            },
            divider = {},
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selectedTab == index) ArcanumGold else ArcanumGrayMid,
                        )
                    },
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ArcanumBorder),
        )

        when (selectedTab) {
            0 -> SoundscapesLibraryTab(
                categories = state.soundscapeCategories,
                isLoading = state.isLoading,
                onOpenComposer = onOpenComposer,
                onAdd = { showAddCategoryDialog = true },
            )
            1 -> FxLibraryTab(
                effects = state.fxEffects,
                isLoading = state.isLoading,
                onImport = { showImportFxDialog = true },
                onDelete = viewModel::deleteEffect,
            )
        }
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name, parent ->
                viewModel.addSoundscapeCategory(name, parent)
                showAddCategoryDialog = false
            },
        )
    }

    if (showImportFxDialog) {
        ImportFxDialog(
            onDismiss = { showImportFxDialog = false },
            onConfirm = { name, path, tags ->
                viewModel.importFxEffect(name, path, tags)
                showImportFxDialog = false
            },
        )
    }
}

@Composable
private fun SoundscapesLibraryTab(
    categories: List<SoundscapeCategory>,
    isLoading: Boolean,
    onOpenComposer: (Long) -> Unit,
    onAdd: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        if (categories.isEmpty() && !isLoading) {
            item {
                EmptyState(
                    title = "No Soundscape Categories",
                    subtitle = "Create categories to build rich atmospheres.",
                )
            }
        }

        items(categories, key = { it.id }) { category ->
            SoundscapeCategoryRow(
                category = category,
                onEdit = { onOpenComposer(category.id) },
            )
        }

        item {
            PrimaryButton(
                text = "+ NEW SOUNDSCAPE",
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SoundscapeCategoryRow(
    category: SoundscapeCategory,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ArcanumCardSurface)
            .clickable { onEdit() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
            if (category.parentCategory.isNotBlank()) {
                Text(
                    text = category.parentCategory.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcanumGrayMid,
                )
            }
        }
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Filled.Create,
                contentDescription = "Edit ${category.name}",
                tint = ArcanumGold,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun FxLibraryTab(
    effects: List<FxEffect>,
    isLoading: Boolean,
    onImport: () -> Unit,
    onDelete: (FxEffect) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        if (effects.isEmpty() && !isLoading) {
            item {
                EmptyState(
                    title = "No FX in Library",
                    subtitle = "Import sound effects to use on your soundboard.",
                )
            }
        }

        items(effects, key = { it.id }) { effect ->
            FxEffectRow(effect = effect, onPlay = { /* playback */ })
        }

        item {
            PrimaryButton(
                text = "IMPORT FX",
                onClick = onImport,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FxEffectRow(effect: FxEffect, onPlay: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ArcanumCardSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = effect.name, style = MaterialTheme.typography.titleMedium, color = Color.White)
            if (effect.tags.isNotEmpty()) {
                Text(
                    text = effect.tags.joinToString(", ").uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcanumGrayMid,
                )
            }
        }
        Text(
            text = "${effect.playCount}×",
            style = MaterialTheme.typography.labelSmall,
            color = ArcanumGrayLight,
            modifier = Modifier.padding(end = 8.dp),
        )
        IconButton(onClick = onPlay) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play ${effect.name}",
                tint = ArcanumGold,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var parent by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Soundscape Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = parent,
                    onValueChange = { parent = it },
                    label = { Text("Parent Category (e.g. ENVIRONMENT)") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), parent.trim()) },
            ) {
                Text("CREATE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        },
    )
}

@Composable
private fun ImportFxDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, List<String>) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var path by remember { mutableStateOf("") }
    var tagsRaw by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import FX") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("FX Name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = path,
                    onValueChange = { path = it },
                    label = { Text("File Path or URI") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = tagsRaw,
                    onValueChange = { tagsRaw = it },
                    label = { Text("Tags (comma-separated)") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val tags = tagsRaw.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        onConfirm(name.trim(), path.trim(), tags)
                    }
                },
            ) {
                Text("IMPORT", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        },
    )
}
