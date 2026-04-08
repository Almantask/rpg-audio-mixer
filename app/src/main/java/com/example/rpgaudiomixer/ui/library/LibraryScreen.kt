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
    modifier: Modifier = Modifier,
    onNavigateToComposer: (Long) -> Unit = {}
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
                SoundscapeLibraryScreenTab(
                    modifier = Modifier.padding(paddingValues),
                    onNavigateToComposer = onNavigateToComposer
                )
            }
            LibraryTab.SOUND_EFFECTS -> {
                FxLibraryScreenTab(
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun SoundscapeLibraryScreenTab(
    modifier: Modifier = Modifier,
    onNavigateToComposer: (Long) -> Unit
) {
    com.example.rpgaudiomixer.ui.library.soundscapes.SoundscapeLibraryScreen(
        modifier = modifier,
        onNavigateToComposer = onNavigateToComposer
    )
}

@Composable
private fun FxLibraryScreenTab(modifier: Modifier = Modifier) {
    com.example.rpgaudiomixer.ui.library.fx.FxLibraryScreen(
        modifier = modifier
    )
}
