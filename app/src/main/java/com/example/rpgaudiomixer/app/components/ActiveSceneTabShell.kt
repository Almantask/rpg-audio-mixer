package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class ActiveSceneTab {
    SOUNDSCAPES,
    SOUNDBOARD,
}

@Composable
fun ActiveSceneTabShell(
    activeTab: ActiveSceneTab,
    onSelectSoundscapes: () -> Unit,
    onSelectSoundboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        if (activeTab == ActiveSceneTab.SOUNDSCAPES) {
            Button(onClick = onSelectSoundscapes) {
                Text("Soundscapes")
            }
        } else {
            OutlinedButton(onClick = onSelectSoundscapes) {
                Text("Soundscapes")
            }
        }

        if (activeTab == ActiveSceneTab.SOUNDBOARD) {
            Button(onClick = onSelectSoundboard) {
                Text("Soundboard")
            }
        } else {
            OutlinedButton(onClick = onSelectSoundboard) {
                Text("Soundboard")
            }
        }
    }
}
