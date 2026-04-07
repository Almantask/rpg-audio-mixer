package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.rpgaudiomixer.app.components.ArcanumTopBar

enum class LibraryTab {
    SOUNDSCAPES,
    SOUND_EFFECTS
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.SOUNDSCAPES) }

    Scaffold(
        topBar = {
            Column {
                ArcanumTopBar(
                    title = "Audio Library",
                    showBackArrow = false,
                    onBack = {},
                    onGearClick = { /* Navigate to settings */ }
                )
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == LibraryTab.SOUNDSCAPES,
                        onClick = { selectedTab = LibraryTab.SOUNDSCAPES },
                        text = { Text("Soundscapes") }
                    )
                    Tab(
                        selected = selectedTab == LibraryTab.SOUND_EFFECTS,
                        onClick = { selectedTab = LibraryTab.SOUND_EFFECTS },
                        text = { Text("Sound Effects") }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            LibraryTab.SOUNDSCAPES -> {
                SoundscapeLibraryScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
            LibraryTab.SOUND_EFFECTS -> {
                FxLibraryScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun SoundscapeLibraryScreen(modifier: Modifier = Modifier) {
    // Placeholder for now - will be implemented next
    Text(
        text = "Soundscape Library - Coming Soon",
        modifier = modifier.padding(16.dp)
    )
}

@Composable
private fun FxLibraryScreen(modifier: Modifier = Modifier) {
    // Placeholder for now - will be implemented in Iteration 4
    Text(
        text = "FX Library - Coming Soon",
        modifier = modifier.padding(16.dp)
    )
}
