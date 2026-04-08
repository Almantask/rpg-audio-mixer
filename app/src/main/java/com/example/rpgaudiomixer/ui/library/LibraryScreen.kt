package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.ui.fx.FxLibraryRoute
import com.example.rpgaudiomixer.ui.fx.FxLibraryViewModel
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeLibraryRoute

enum class LibraryTab(
    val title: String,
) {
    SOUNDSCAPES("Soundscapes"),
    SOUND_EFFECTS("Sound Effects"),
}

@Composable
fun LibraryRoute(
    onOpenComposer: (Long) -> Unit,
    modifier: Modifier = Modifier,
    initialTab: LibraryTab = LibraryTab.SOUNDSCAPES,
    fxLibraryViewModel: FxLibraryViewModel = hiltViewModel(),
) {
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }

    LaunchedEffect(selectedTab) {
        if (selectedTab != LibraryTab.SOUND_EFFECTS) {
            fxLibraryViewModel.stopPreview()
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TabRow(selectedTabIndex = selectedTab.ordinal) {
            LibraryTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = { Text(text = tab.title) },
                )
            }
        }
        when (selectedTab) {
            LibraryTab.SOUNDSCAPES -> SoundscapeLibraryRoute(
                onOpenComposer = onOpenComposer,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
            )

            LibraryTab.SOUND_EFFECTS -> FxLibraryRoute(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                viewModel = fxLibraryViewModel,
            )
        }
    }
}
