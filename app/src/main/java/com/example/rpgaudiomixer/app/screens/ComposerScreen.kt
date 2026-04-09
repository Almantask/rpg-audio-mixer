package com.example.rpgaudiomixer.app.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.IntensitySelector
import com.example.rpgaudiomixer.app.components.MixSlider
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.ui.library.SoundscapeCategoryComposerUiState
import com.example.rpgaudiomixer.app.ui.library.SoundscapeCategoryComposerViewModel
import com.example.rpgaudiomixer.app.util.FileUtil
import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.library.SoundscapeTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposerScreen(
    viewModel: SoundscapeCategoryComposerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf<Uri?>(null) }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { showImportDialog = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = uiState) {
                        is SoundscapeCategoryComposerUiState.Success -> Text(state.category.name, color = ArcanumGold)
                        else -> Text("Composer", color = ArcanumGold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ArcanumGold)
                    }
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = ArcanumGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArcanumBlack)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { audioPicker.launch(arrayOf("audio/*")) },
                containerColor = ArcanumGold,
                contentColor = ArcanumOnGold,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("INVOKE NEW SOUND") }
            )
        },
        containerColor = ArcanumBlack
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is SoundscapeCategoryComposerUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = ArcanumGold)
                }
                is SoundscapeCategoryComposerUiState.Success -> {
                    if (state.tracks.isEmpty()) {
                        EmptyComposerState()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.tracks, key = { it.id }) { track ->
                                SwipeToDeleteContainer(onDelete = { viewModel.deleteTrack(track.id) }) {
                                    TrackComposerCard(
                                        track = track,
                                        onIntensityChange = { viewModel.updateTrackIntensity(track, it) },
                                        onVolumeChange = { viewModel.updateTrackVolume(track, it) }
                                    )
                                }
                            }
                        }
                    }
                }
                is SoundscapeCategoryComposerUiState.Error -> {
                    Text(text = state.message, color = ArcanumErrorRed, modifier = Modifier.align(Alignment.Center))
                }
            }

            showImportDialog?.let { uri ->
                ImportTrackDialog(
                    onDismiss = { showImportDialog = null },
                    onConfirm = { name ->
                        val path = FileUtil.copyAudioToInternal(context, uri)
                        if (path != null) {
                            viewModel.addTrack(name, path)
                        }
                        showImportDialog = null
                    }
                )
            }
        }
    }
}

@Composable
fun TrackComposerCard(
    track: SoundscapeTrack,
    onIntensityChange: (IntensityLevel) -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArcanumCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = track.name,
                style = MaterialTheme.typography.titleMedium,
                color = ArcanumGold,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            IntensitySelector(
                selectedLevel = track.intensityLevel,
                onLevelSelected = onIntensityChange,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MixSlider(
                value = track.mixVolume,
                onValueChange = onVolumeChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EmptyComposerState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = ArcanumOnSurface.copy(alpha = 0.2f)
        )
        Text(
            "Every category starts with a single note.",
            color = ArcanumOnSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun ImportTrackDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumCard,
        title = { Text("Import Track", color = ArcanumGold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Track Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ArcanumGold,
                    focusedLabelColor = ArcanumGold,
                    unfocusedLabelColor = ArcanumOnSurface.copy(alpha = 0.7f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("SCRIBE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = ArcanumOnSurface)
            }
        }
    )
}
