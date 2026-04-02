package com.example.rpgaudiomixer.app.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.GradientSlider
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumCard
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDim
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurface
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceVariant
import com.example.rpgaudiomixer.app.theme.ArcanumPink
import com.example.rpgaudiomixer.app.theme.ArcanumPurple
import com.example.rpgaudiomixer.app.theme.ArcanumSurface
import com.example.rpgaudiomixer.app.viewmodel.SoundscapeComposerViewModel
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Track

@Composable
fun SoundscapeComposerScreen(
    onBack: () -> Unit,
    onCredits: () -> Unit,
    viewModel: SoundscapeComposerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var pendingFilePath by remember { mutableStateOf<String?>(null) }

    // Navigate back when saved
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
    }

    val context = LocalContext.current
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
            pendingFilePath = it.toString()
            showAddDialog = true
        }
    }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = if (state.isNewCategory) "NEW COMPOSITION" else "COMPOSITION",
                onBack = onBack,
                onCredits = onCredits,
            )
        },
        containerColor = ArcanumBlack,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
        ) {
            // Category name field
            item {
                ArcanumTextField(
                    label = "Category Name",
                    value = state.categoryName,
                    onValueChange = viewModel::setCategoryName,
                )
            }

            item { Spacer(Modifier.height(4.dp)) }

            // Section header
            if (state.tracks.isNotEmpty()) {
                item {
                    Text(
                        "TRACKS",
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        color = ArcanumGoldDim,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Track list
            items(state.tracks, key = { it.id }) { track ->
                TrackComposerRow(
                    track = track,
                    onVolumeChange = { volume -> viewModel.updateTrackVolume(track.id, volume) },
                    onDelete = { viewModel.deleteTrack(track.id) },
                )
            }

            // Empty state
            if (state.tracks.isEmpty()) {
                item {
                    EmptyComposerState()
                }
            }

            // Invoke (add) track button
            item {
                Button(
                    onClick = { filePicker.launch(arrayOf("audio/*")) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ArcanumPurple.copy(alpha = 0.15f),
                        contentColor = ArcanumPurple,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ArcanumPurple.copy(alpha = 0.35f)),
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("INVOKE NEW SOUNDSCAPE", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            // Save button
            item {
                Button(
                    onClick = viewModel::save,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.categoryName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ArcanumGold.copy(alpha = 0.15f),
                        contentColor = ArcanumGold,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.categoryName.isNotBlank()) ArcanumGoldDim.copy(alpha = 0.5f) else ArcanumGoldDim.copy(alpha = 0.15f),
                    ),
                ) {
                    Icon(Icons.Default.LibraryMusic, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("SAVE COMPOSITION", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }

    // Add track dialog (triggered after file pick)
    if (showAddDialog && pendingFilePath != null) {
        AddTrackDialog(
            filePath = pendingFilePath!!,
            onConfirm = { name, intensity ->
                viewModel.addTrack(name, pendingFilePath!!, intensity)
                pendingFilePath = null
                showAddDialog = false
            },
            onDismiss = {
                pendingFilePath = null
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun TrackComposerRow(
    track: Track,
    onVolumeChange: (Float) -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ArcanumCard)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Intensity badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(ArcanumPurple.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    track.intensityLevel.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArcanumPurple,
                    letterSpacing = 0.5.sp,
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = track.name,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = ArcanumOnSurface,
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.DeleteOutline, "Delete", tint = ArcanumOnSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
            }
        }

        // Mix volume slider
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("MIX", fontSize = 9.sp, letterSpacing = 1.sp, color = ArcanumOnSurfaceVariant, modifier = Modifier.width(28.dp))
            GradientSlider(
                value = track.mixVolume,
                onValueChange = onVolumeChange,
                modifier = Modifier.weight(1f),
            )
            Text(
                "${(track.mixVolume * 100).toInt()}%",
                fontSize = 10.sp,
                color = ArcanumOnSurfaceVariant,
                modifier = Modifier.width(32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
            )
        }
    }
}

@Composable
private fun EmptyComposerState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Default.LibraryMusic,
            null,
            tint = ArcanumGoldDim.copy(alpha = 0.4f),
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "No tracks yet. Invoke a soundscape to begin.",
            fontSize = 13.sp,
            color = ArcanumOnSurfaceVariant.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun AddTrackDialog(
    filePath: String,
    onConfirm: (String, IntensityLevel) -> Unit,
    onDismiss: () -> Unit,
) {
    val defaultName = filePath.substringAfterLast("/").substringBeforeLast(".")
    var name by remember { mutableStateOf(defaultName) }
    var selectedIntensity by remember { mutableStateOf(IntensityLevel.I) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumSurface,
        title = { Text("INVOKE SOUNDSCAPE", fontFamily = FontFamily.Serif, color = ArcanumGold, fontSize = 17.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ArcanumTextField("Track Name", name, { name = it })
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("INTENSITY LEVEL", fontSize = 9.sp, letterSpacing = 1.5.sp, color = ArcanumGoldDim, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IntensityLevel.entries.forEach { level ->
                            val isSelected = selectedIntensity == level
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) ArcanumGold.copy(alpha = 0.2f) else ArcanumSurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) ArcanumGold else ArcanumGoldDim.copy(alpha = 0.3f),
                                        RoundedCornerShape(6.dp),
                                    )
                                    .clickable { selectedIntensity = level }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    level.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ArcanumGold else ArcanumOnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), selectedIntensity) },
                enabled = name.isNotBlank(),
            ) {
                Text("INVOKE", color = if (name.isNotBlank()) ArcanumGold else ArcanumOnSurfaceVariant, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = ArcanumOnSurfaceVariant) }
        },
    )
}
