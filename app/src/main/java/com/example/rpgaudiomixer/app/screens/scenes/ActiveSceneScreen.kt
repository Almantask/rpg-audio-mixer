package com.example.rpgaudiomixer.app.screens.scenes

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

enum class ActiveSceneTab {
    SOUNDSCAPES,
    SOUNDBOARD
}

@Composable
fun ActiveSceneScreen(
    sceneId: Long,
    sessionId: Long = -1,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(ActiveSceneTab.SOUNDSCAPES) }

    when (selectedTab) {
        ActiveSceneTab.SOUNDSCAPES -> {
            ActiveSceneSoundscapesScreen(
                sceneId = sceneId,
                onBack = onBack,
                onSwitchToSoundboard = { selectedTab = ActiveSceneTab.SOUNDBOARD }
            )
        }
        ActiveSceneTab.SOUNDBOARD -> {
            ActiveSceneSoundboardScreen(
                sceneId = sceneId,
                onBack = onBack,
                onSwitchToSoundscapes = { selectedTab = ActiveSceneTab.SOUNDSCAPES }
            )
        }
    }
}
