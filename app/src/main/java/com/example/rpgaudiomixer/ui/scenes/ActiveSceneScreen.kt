package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.playback.ScenePlaybackController
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

object ActiveSceneTestTags {
    const val SCREEN = "Screen_ActiveScene"
}

@Composable
fun ActiveSceneRoute(
    onOpenSoundscapeComposer: (Long) -> Unit = {},
    viewModel: ActiveSceneViewModel = hiltViewModel(),
) {
    val scene by viewModel.scene.collectAsState(initial = null)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(scene?.id, viewModel.autoplay) {
        if (scene != null && viewModel.autoplay) {
            viewModel.startPlayback()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag(ActiveSceneTestTags.SCREEN),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp),
            text = "Active Scene: ${scene?.name.orEmpty()}",
        )
        TabRow(selectedTabIndex = selectedTabIndex) {
            listOf("Soundscapes", "Soundboard").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                )
            }
        }
        AnimatedContent(
            targetState = selectedTabIndex,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (slideInHorizontally(animationSpec = tween(220)) { fullWidth -> direction * fullWidth / 4 } + fadeIn(animationSpec = tween(220)))
                    togetherWith(slideOutHorizontally(animationSpec = tween(220)) { fullWidth -> -direction * fullWidth / 4 } + fadeOut(animationSpec = tween(220)))
            },
            label = "active-scene-tab-switch",
        ) { tabIndex ->
            if (tabIndex == 0) {
                ActiveSceneSoundscapesRoute(
                    modifier = Modifier.fillMaxSize(),
                    onOpenSoundscapeComposer = onOpenSoundscapeComposer,
                )
            } else {
                ActiveSceneSoundboardRoute(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@HiltViewModel
class ActiveSceneViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val scenePlaybackController: ScenePlaybackController,
) : ViewModel() {
    private val sceneId = requireNotNull(savedStateHandle.get<String>("sceneId")) {
        "Navigation argument 'sceneId' is missing."
    }.toLongOrNull() ?: error("Navigation argument 'sceneId' must be a valid numeric value.")

    val autoplay: Boolean = savedStateHandle.get<String>("autoplay")?.toBooleanStrictOrNull() ?: false
    val scene: Flow<Scene?> = sceneRepository.observeScene(sceneId)

    fun startPlayback() {
        viewModelScope.launch {
            scenePlaybackController.playScene(sceneId)
        }
    }
}
