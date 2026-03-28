package com.example.rpgaudiomixer.ui.soundscapecomposer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.theme.ArcanumBorder
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDark
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.app.theme.ArcanumGrayMid
import com.example.rpgaudiomixer.app.theme.IntensityColorI
import com.example.rpgaudiomixer.app.theme.IntensityColorII
import com.example.rpgaudiomixer.app.theme.IntensityColorIII
import com.example.rpgaudiomixer.domain.model.SoundscapeLayer
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.EmptyState
import com.example.rpgaudiomixer.ui.components.PrimaryButton

@Composable
fun SoundscapeComposerScreen(
    categoryId: Long,
    onBack: () -> Unit,
    onCredits: () -> Unit,
    viewModel: SoundscapeComposerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState(categoryId).collectAsStateWithLifecycle()
    var showAddLayerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(
            onCredits = onCredits,
            showBack = true,
            onBack = onBack,
        )

        state.category?.let { category ->
            Text(
                text = category.name,
                style = MaterialTheme.typography.displaySmall,
                color = ArcanumGold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            if (category.parentCategory.isNotBlank()) {
                Text(
                    text = category.parentCategory.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = ArcanumGrayMid,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            if (state.layers.isEmpty() && !state.isLoading) {
                item {
                    EmptyState(
                        title = "No Layers",
                        subtitle = "Invoke a new layer to compose the atmosphere.",
                    )
                }
            }

            items(state.layers, key = { it.id }) { layer ->
                SoundscapeLayerCard(
                    layer = layer,
                    onIntensityChange = { intensity ->
                        viewModel.updateLayerIntensity(layer, intensity)
                    },
                    onMixChange = { mix -> viewModel.updateLayerMix(layer, mix) },
                    onRemove = { viewModel.removeLayer(layer) },
                )
            }

            // "Invoke New Layer" dashed card
            item {
                InvokeLayerCard(onClick = { showAddLayerDialog = true })
            }

            item {
                state.category?.let { category ->
                    PrimaryButton(
                        text = "SAVE COMPOSITION",
                        onClick = { viewModel.saveCategory(category, category.name) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showAddLayerDialog) {
        AddLayerDialog(
            onDismiss = { showAddLayerDialog = false },
            onConfirm = { name, path, intensity ->
                viewModel.addLayer(categoryId, name, path, intensity)
                showAddLayerDialog = false
            },
        )
    }
}

@Composable
private fun SoundscapeLayerCard(
    layer: SoundscapeLayer,
    onIntensityChange: (Int) -> Unit,
    onMixChange: (Float) -> Unit,
    onRemove: () -> Unit,
) {
    var mix by remember(layer.mix) { mutableFloatStateOf(layer.mix) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCardSurface)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = layer.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                )
                Text(
                    text = layer.trackFilePath.substringAfterLast("/").ifBlank { layer.trackFilePath },
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcanumGrayMid,
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Remove ${layer.name}",
                    tint = ArcanumGrayMid,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Intensity selector
        Text(
            text = "INTENSITY",
            style = MaterialTheme.typography.labelSmall,
            color = ArcanumGrayLight,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1 to "Level I", 2 to "Level II", 3 to "Level III").forEach { (level, label) ->
                val isSelected = layer.intensity == level
                val color = when (level) {
                    1 -> IntensityColorI
                    2 -> IntensityColorII
                    else -> IntensityColorIII
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) color else ArcanumBorder,
                            RoundedCornerShape(6.dp),
                        )
                        .clickable { onIntensityChange(level) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) color else ArcanumGrayMid,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // MIX slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "MIX",
                style = MaterialTheme.typography.labelSmall,
                color = ArcanumGrayLight,
                modifier = Modifier.width(36.dp),
            )
            Slider(
                value = mix,
                onValueChange = { mix = it },
                onValueChangeFinished = { onMixChange(mix) },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = ArcanumGold,
                    activeTrackColor = ArcanumGoldDark,
                    inactiveTrackColor = ArcanumBorder,
                ),
            )
            Text(
                text = "${(mix * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = ArcanumGold,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun InvokeLayerCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, ArcanumGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = ArcanumGold.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "  ✦ INVOKE NEW LAYER",
                style = MaterialTheme.typography.titleMedium,
                color = ArcanumGold.copy(alpha = 0.7f),
            )
        }
    }
}

private fun getDisplayName(context: Context, uri: Uri): String =
    context.contentResolver.query(
        uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null,
    )?.use { cursor ->
        if (cursor.moveToFirst()) cursor.getString(0) else null
    } ?: uri.lastPathSegment ?: uri.toString()

@Composable
private fun AddLayerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit,
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var intensity by remember { mutableIntStateOf(1) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
            pickedUri = uri
            if (name.isBlank()) {
                name = getDisplayName(context, uri).substringBeforeLast(".")
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Layer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Layer Name") },
                    singleLine = true,
                )
                // File picker row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = pickedUri?.let { getDisplayName(context, it) } ?: "No file selected",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (pickedUri != null) ArcanumGrayLight else ArcanumGrayMid,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = { audioPickerLauncher.launch(arrayOf("audio/*")) }) {
                        Text("BROWSE", color = ArcanumGold)
                    }
                }
                Text("Intensity", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1 to "I", 2 to "II", 3 to "III").forEach { (level, label) ->
                        val isSelected = intensity == level
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) ArcanumGold else ArcanumBorder,
                                    RoundedCornerShape(4.dp),
                                )
                                .clickable { intensity = level }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) ArcanumGold else ArcanumGrayLight,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && pickedUri != null) {
                        onConfirm(name.trim(), pickedUri.toString(), intensity)
                    }
                },
                enabled = name.isNotBlank() && pickedUri != null,
            ) {
                Text("INVOKE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        },
    )
}
