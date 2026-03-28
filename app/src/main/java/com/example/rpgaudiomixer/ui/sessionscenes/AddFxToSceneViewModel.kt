package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.FxEffect
import com.example.rpgaudiomixer.domain.storage.FxRepository
import com.example.rpgaudiomixer.domain.storage.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddFxUiState(
    val effects: List<FxEffect> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class AddFxToSceneViewModel @Inject constructor(
    private val fxRepository: FxRepository,
    private val sceneRepository: SceneRepository,
) : ViewModel() {

    fun uiState(sceneId: Long): StateFlow<AddFxUiState> =
        fxRepository.getAllEffects()
            .map { fx -> AddFxUiState(effects = fx.sortedBy { it.name }, isLoading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AddFxUiState(),
            )

    fun addFx(sceneId: Long, fxEffectId: Long) {
        viewModelScope.launch {
            sceneRepository.addFxToScene(sceneId, fxEffectId)
        }
    }
}
