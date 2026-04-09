package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
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
            .padding(24.dp)
            .testTag(ActiveSceneTestTags.SCREEN),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Active Scene: ${scene?.name.orEmpty()}")
        TabRow(selectedTabIndex = selectedTabIndex) {
            listOf("Soundscapes", "Soundboard").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                )
            }
        }
        if (selectedTabIndex == 0) {
            if (scene?.soundscapeCategoryNames.isNullOrEmpty()) {
                Text("No soundscapes yet")
            } else {
                TagRow(tags = scene?.soundscapeCategoryNames.orEmpty())
            }
        } else {
            Text("No soundboard effects yet")
        }
    }
}

@HiltViewModel
class ActiveSceneViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val mixedMusicPlayer: MixedMusicPlayer,
) : ViewModel() {
    private val sceneId = requireNotNull(savedStateHandle.get<String>("sceneId")) {
        "Navigation argument 'sceneId' is missing."
    }.toLongOrNull() ?: error("Navigation argument 'sceneId' must be a valid numeric value.")

    val autoplay: Boolean = savedStateHandle.get<String>("autoplay")?.toBooleanStrictOrNull() ?: false
    val scene: Flow<Scene?> = sceneRepository.observeScene(sceneId)

    fun startPlayback() {
        viewModelScope.launch {
            mixedMusicPlayer.playLoopingSound("scene:$sceneId")
        }
    }
}
