package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.navigation.AppRoute
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class ActiveSceneUiState(
    val scene: Scene? = null,
    val autoplay: Boolean = false,
)

@Composable
fun ActiveSceneRoute(
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ActiveSceneScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
fun ActiveSceneScreen(
    uiState: ActiveSceneUiState,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = uiState.scene?.name ?: "Scene",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = if (uiState.autoplay) {
                "Playback begins with a fade-in."
            } else {
                "No audio is playing."
            },
            style = MaterialTheme.typography.bodyLarge,
        )
        TabRow(selectedTabIndex = selectedTab) {
            listOf("Soundscapes", "Soundboard").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title) },
                )
            }
        }
        Text(
            text = if (selectedTab == 0) {
                "Soundscapes tab"
            } else {
                "Soundboard tab"
            },
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@HiltViewModel
class ActiveSceneViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    sceneRepository: SceneRepository,
) : ViewModel() {
    private val sceneId: Long = requireNotNull(savedStateHandle[AppRoute.SCENE_ID_ARG])
    private val autoplay: Boolean = savedStateHandle[AppRoute.AUTOPLAY_ARG] ?: false

    private val _uiState = MutableStateFlow(ActiveSceneUiState(autoplay = autoplay))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sceneRepository.observeScene(sceneId).collect { scene ->
                _uiState.value = ActiveSceneUiState(
                    scene = scene,
                    autoplay = autoplay,
                )
            }
        }
    }
}
