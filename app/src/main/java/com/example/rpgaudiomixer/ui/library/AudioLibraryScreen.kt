package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class AudioLibraryTab(val label: String) {
    SOUNDSCAPES("Soundscapes"),
    SOUND_EFFECTS("Sound Effects"),
}

@Composable
fun AudioLibraryScreen(
    onOpenComposer: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(AudioLibraryTab.SOUNDSCAPES) }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            AudioLibraryTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.label) },
                )
            }
        }
        when (selectedTab) {
            AudioLibraryTab.SOUNDSCAPES -> SoundscapeLibraryScreen(
                onOpenComposer = onOpenComposer,
                modifier = Modifier.padding(top = 8.dp),
            )
            AudioLibraryTab.SOUND_EFFECTS -> FxLibraryScreen(
                isVisible = true,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
