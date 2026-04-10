package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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

    AnimatedContent(
        targetState = activeTab,
        modifier = modifier,
        transitionSpec = {
            val forward = targetState.ordinal > initialState.ordinal
            ContentTransform(
                targetContentEnter = fadeIn() + slideInHorizontally { fullWidth ->
                    if (forward) fullWidth / 5 else -fullWidth / 5
                },
                initialContentExit = fadeOut() + slideOutHorizontally { fullWidth ->
                    if (forward) -fullWidth / 5 else fullWidth / 5
                },
            )
        },
        label = "activeSceneTab",
    ) { targetTab ->
        when (targetTab) {
            ActiveSceneTab.SOUNDSCAPES -> ActiveSceneSoundscapesRoute(
                onTitleChange = onTitleChange,
                onOpenSoundboard = { activeTab = ActiveSceneTab.SOUNDBOARD },
                modifier = Modifier,
            )

            ActiveSceneTab.SOUNDBOARD -> ActiveSceneSoundboardRoute(
                onTitleChange = onTitleChange,
                onOpenSoundscapes = { activeTab = ActiveSceneTab.SOUNDSCAPES },
                modifier = Modifier,
            )
        }
    }
}
