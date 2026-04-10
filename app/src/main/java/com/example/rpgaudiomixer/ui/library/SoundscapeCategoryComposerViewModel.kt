package com.example.rpgaudiomixer.ui.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrackDraft
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditableSoundscapeTrack(
    val localId: Long,
    val persistedId: Long? = null,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float,
)

data class SoundscapeComposerEditorState(
    val categoryName: String,
    val tracks: List<EditableSoundscapeTrack>,
    val hasUnsavedChanges: Boolean,
    val isNewCategory: Boolean,
)

@HiltViewModel
class SoundscapeCategoryComposerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val soundscapeRepository: SoundscapeRepository,
) : ViewModel() {

    private val categoryIdArg: Long =
        checkNotNull(savedStateHandle[MainNavDestination.SOUNDSCAPE_CATEGORY_ID_ARG])
    private val initialCategoryNameArg: String =
        savedStateHandle[MainNavDestination.SOUNDSCAPE_CATEGORY_NAME_ARG] ?: ""

    private val _uiState = MutableStateFlow<UiState<SoundscapeComposerEditorState>>(UiState.Loading)
    val uiState: StateFlow<UiState<SoundscapeComposerEditorState>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var baselineState: SoundscapeComposerEditorState? = null
    private var nextLocalTrackId = -1L

    init {
        if (categoryIdArg > 0) {
            loadExistingCategory()
        } else {
            val initialState = SoundscapeComposerEditorState(
                categoryName = initialCategoryNameArg,
                tracks = emptyList(),
                hasUnsavedChanges = false,
                isNewCategory = true,
            )
            baselineState = initialState
            _uiState.value = UiState.Success(initialState)
        }
    }

    fun updateCategoryName(name: String) {
        updateEditor { editorState ->
            editorState.copy(categoryName = name)
        }
    }

    fun addImportedTrack(displayName: String, filePath: String) {
        updateEditor { editorState ->
            editorState.copy(
                tracks = editorState.tracks + EditableSoundscapeTrack(
                    localId = nextLocalTrackId--,
                    name = displayName,
                    filePath = filePath,
                    intensityLevel = IntensityLevel.I,
                    mixVolume = 1f,
                ),
            )
        }
    }

    fun updateTrackName(localId: Long, name: String) {
        updateTrack(localId) { track ->
            track.copy(name = name)
        }
    }

    fun updateTrackIntensity(localId: Long, intensityLevel: IntensityLevel) {
        updateTrack(localId) { track ->
            track.copy(intensityLevel = intensityLevel)
        }
    }

    fun updateTrackMix(localId: Long, mixVolume: Float) {
        updateTrack(localId) { track ->
            track.copy(mixVolume = mixVolume)
        }
    }

    fun removeTrack(localId: Long) {
        updateEditor { editorState ->
            editorState.copy(
                tracks = editorState.tracks.filterNot { track -> track.localId == localId },
            )
        }
    }

    fun reportImportFailure(message: String) {
        _errorMessage.value = message
    }

    fun saveComposition(onSaved: () -> Unit) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val trimmedName = currentState.categoryName.trim()

        if (trimmedName.isEmpty()) {
            _errorMessage.value = "Category name is required."
            return
        }

        if (currentState.tracks.any { track -> track.name.trim().isEmpty() }) {
            _errorMessage.value = "Every soundscape needs a name before saving."
            return
        }

        viewModelScope.launch {
            runCatching {
                soundscapeRepository.saveComposition(
                    categoryId = categoryIdArg.takeIf { it > 0 },
                    name = trimmedName,
                    tracks = currentState.tracks.map { track ->
                        SoundscapeTrackDraft(
                            id = track.persistedId,
                            name = track.name.trim(),
                            filePath = track.filePath,
                            intensityLevel = track.intensityLevel,
                            mixVolume = track.mixVolume.coerceIn(0f, 1f),
                        )
                    },
                )
            }.onSuccess {
                onSaved()
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to save composition."
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    private fun loadExistingCategory() {
        viewModelScope.launch {
            runCatching {
                val category = soundscapeRepository.getCategory(categoryIdArg)
                    ?: error("Soundscape category not found.")
                val tracks = soundscapeRepository.getTracks(categoryIdArg)
                SoundscapeComposerEditorState(
                    categoryName = category.name,
                    tracks = tracks.map { track ->
                        EditableSoundscapeTrack(
                            localId = track.id,
                            persistedId = track.id,
                            name = track.name,
                            filePath = track.filePath,
                            intensityLevel = track.intensityLevel,
                            mixVolume = track.mixVolume,
                        )
                    },
                    hasUnsavedChanges = false,
                    isNewCategory = false,
                )
            }.onSuccess { editorState ->
                baselineState = editorState
                _uiState.value = UiState.Success(editorState)
            }.onFailure { throwable ->
                _uiState.value = UiState.Error(
                    throwable.message ?: "Unable to load soundscape composition.",
                )
            }
        }
    }

    private fun updateTrack(
        localId: Long,
        transform: (EditableSoundscapeTrack) -> EditableSoundscapeTrack,
    ) {
        updateEditor { editorState ->
            editorState.copy(
                tracks = editorState.tracks.map { track ->
                    if (track.localId == localId) {
                        transform(track)
                    } else {
                        track
                    }
                },
            )
        }
    }

    private fun updateEditor(
        transform: (SoundscapeComposerEditorState) -> SoundscapeComposerEditorState,
    ) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val updatedState = transform(currentState)
        val baseline = baselineState
        _uiState.value = UiState.Success(
            updatedState.copy(
                hasUnsavedChanges = baseline?.let { previous ->
                    updatedState.copy(hasUnsavedChanges = false) != previous.copy(hasUnsavedChanges = false)
                } ?: true,
            ),
        )
    }
}
