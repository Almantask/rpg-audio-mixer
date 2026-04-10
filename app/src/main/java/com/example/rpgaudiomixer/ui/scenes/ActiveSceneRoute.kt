package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.rpgaudiomixer.app.components.ActiveSceneTab

@Composable
fun ActiveSceneRoute(
    onTitleChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var activeTab by rememberSaveable { mutableStateOf(ActiveSceneTab.SOUNDSCAPES) }

    when (activeTab) {
        ActiveSceneTab.SOUNDSCAPES -> ActiveSceneSoundscapesRoute(
            onTitleChange = onTitleChange,
            onOpenSoundboard = { activeTab = ActiveSceneTab.SOUNDBOARD },
            modifier = modifier,
        )

        ActiveSceneTab.SOUNDBOARD -> ActiveSceneSoundboardRoute(
            onTitleChange = onTitleChange,
            onOpenSoundscapes = { activeTab = ActiveSceneTab.SOUNDSCAPES },
            modifier = modifier,
        )
    }
}
