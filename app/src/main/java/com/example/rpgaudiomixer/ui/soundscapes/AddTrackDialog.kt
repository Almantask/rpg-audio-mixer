package com.example.rpgaudiomixer.ui.soundscapes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.components.IntensitySelector
import com.example.rpgaudiomixer.app.components.MixSlider
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@Composable
fun AddTrackDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, filePath: String, intensity: IntensityLevel, mixVolume: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var trackName by remember { mutableStateOf("") }
    var filePath by remember { mutableStateOf("") }
    var selectedIntensity by remember { mutableStateOf(IntensityLevel.I) }
    var mixVolume by remember { mutableFloatStateOf(0.5f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Invoke New Soundscape",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = trackName,
                    onValueChange = { trackName = it },
                    label = { Text("Track Name") },
                    placeholder = { Text("e.g., Rain on Leaves") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("TrackNameInput")
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = filePath,
                    onValueChange = { filePath = it },
                    label = { Text("File Path") },
                    placeholder = { Text("e.g., sounds/rain.mp3") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("FilePathInput")
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Intensity Level",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                IntensitySelector(
                    selectedIntensity = selectedIntensity,
                    onIntensitySelected = { selectedIntensity = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                MixSlider(
                    label = "MIX",
                    value = mixVolume,
                    onValueChange = { mixVolume = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Note: In production, use file picker to select audio files",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (trackName.isNotBlank() && filePath.isNotBlank()) {
                        onAdd(trackName.trim(), filePath.trim(), selectedIntensity, mixVolume)
                    }
                },
                enabled = trackName.isNotBlank() && filePath.isNotBlank(),
                modifier = Modifier.testTag("ConfirmAddButton")
            ) {
                Text(
                    text = "Add",
                    color = if (trackName.isNotBlank() && filePath.isNotBlank()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("CancelButton")
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("AddTrackDialog")
    )
}
