package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.fx.FxLibraryScreen
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeLibraryScreen

/**
 * Library screen with tabs for Soundscapes and Sound Effects.
 */
@Composable
fun LibraryScreen(
    onNavigateToCategoryComposer: (Long) -> Unit,
    onNavigateToFxEdit: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Soundscapes", "Sound Effects")

    Scaffold(
        topBar = {
            Column {
                ArcanumTopBar(
                    title = "Library",
                    showBackArrow = false,
                    onGearClick = onNavigateToSettings
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.testTag("Library_TabRow")
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                            modifier = Modifier.testTag("Library_Tab_$title")
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> SoundscapeLibraryScreen(
                    onNavigateToCategoryComposer = onNavigateToCategoryComposer,
                    onNavigateToSettings = onNavigateToSettings
                )
                1 -> FxLibraryScreen(
                    onNavigateToFxEdit = onNavigateToFxEdit,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
    }
}
