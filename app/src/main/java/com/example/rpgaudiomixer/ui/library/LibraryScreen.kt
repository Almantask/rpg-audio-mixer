package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.fx.FxLibraryContent
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeLibraryContent

enum class LibraryTab(val title: String) {
    SOUNDSCAPES("Soundscapes"),
    SOUND_EFFECTS("Sound Effects")
}

@Composable
fun LibraryScreen(
    onNavigateToComposer: (Long) -> Unit,
    onNavigateToCredits: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = LibraryTab.entries

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Audio Library",
                showBackArrow = false,
                onGearClick = onNavigateToCredits
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (selectedTab == index) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    )
                }
            }

            when (tabs[selectedTab]) {
                LibraryTab.SOUNDSCAPES -> {
                    SoundscapeLibraryContent(
                        onNavigateToComposer = onNavigateToComposer,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                LibraryTab.SOUND_EFFECTS -> {
                    FxLibraryContent(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
