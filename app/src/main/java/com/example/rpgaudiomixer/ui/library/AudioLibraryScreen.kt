package com.example.rpgaudiomixer.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.screens.MainScreenTestTags
import com.example.rpgaudiomixer.ui.fx.FxLibraryViewModel
import com.example.rpgaudiomixer.ui.fx.FxLibraryPane
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeLibraryRoute

enum class AudioLibraryTab(val label: String) {
    SOUNDSCAPES("Soundscapes"),
    SOUND_EFFECTS("Sound Effects"),
}

object AudioLibraryTestTags {
    const val TAB_ROW = "Library_Tab_Row"
    const val SOUND_SCAPES_TAB = "Library_Tab_Soundscapes"
    const val SOUND_EFFECTS_TAB = "Library_Tab_SoundEffects"
}

@Composable
fun AudioLibraryRoute(
    onOpenSoundscapeComposer: (Long) -> Unit,
    fxLibraryViewModel: FxLibraryViewModel = hiltViewModel(),
) {
    var selectedTab by rememberSaveable { mutableStateOf(AudioLibraryTab.SOUNDSCAPES) }

    DisposableEffect(Unit) {
        onDispose {
            fxLibraryViewModel.stopAndHidePreview()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag(MainScreenTestTags.LIBRARY),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TabRow(
            modifier = Modifier.testTag(AudioLibraryTestTags.TAB_ROW),
            selectedTabIndex = selectedTab.ordinal,
        ) {
            Tab(
                modifier = Modifier.testTag(AudioLibraryTestTags.SOUND_SCAPES_TAB),
                selected = selectedTab == AudioLibraryTab.SOUNDSCAPES,
                onClick = {
                    selectedTab = AudioLibraryTab.SOUNDSCAPES
                    fxLibraryViewModel.stopAndHidePreview()
                },
                text = { Text(AudioLibraryTab.SOUNDSCAPES.label) },
            )
            Tab(
                modifier = Modifier.testTag(AudioLibraryTestTags.SOUND_EFFECTS_TAB),
                selected = selectedTab == AudioLibraryTab.SOUND_EFFECTS,
                onClick = { selectedTab = AudioLibraryTab.SOUND_EFFECTS },
                text = { Text(AudioLibraryTab.SOUND_EFFECTS.label) },
            )
        }

        when (selectedTab) {
            AudioLibraryTab.SOUNDSCAPES -> Box(modifier = Modifier.weight(1f)) {
                SoundscapeLibraryRoute(onOpenComposer = onOpenSoundscapeComposer)
            }
            AudioLibraryTab.SOUND_EFFECTS -> Box(modifier = Modifier.weight(1f)) {
                FxLibraryPane(viewModel = fxLibraryViewModel)
            }
        }
    }
}
