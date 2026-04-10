package com.example.rpgaudiomixer.ui.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

sealed interface SoundscapeCategoryComposerUiState {
    data object Loading : SoundscapeCategoryComposerUiState
    data class Success(
        val categoryName: String,
        val tracks: List<SoundscapeTrack>,
        val hasUnsavedChanges: Boolean,
        val showDiscardChangesDialog: Boolean,
    ) : SoundscapeCategoryComposerUiState

    data class Error(val message: String) : SoundscapeCategoryComposerUiState
}

sealed interface SoundscapeCategoryComposerNavigation {
    data object NavigateBack : SoundscapeCategoryComposerNavigation
}

@HiltViewModel
class SoundscapeCategoryComposerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SoundscapeRepository,
) : ViewModel() {
    private val categoryId: Long = checkNotNull(savedStateHandle["categoryId"])
    private val _uiState = MutableStateFlow<SoundscapeCategoryComposerUiState>(SoundscapeCategoryComposerUiState.Loading)
    val uiState: StateFlow<SoundscapeCategoryComposerUiState> = _uiState.asStateFlow()
    private val _navigationEvents = MutableSharedFlow<SoundscapeCategoryComposerNavigation>()
    val navigationEvents = _navigationEvents

    private var persistedTracks: List<SoundscapeTrack> = emptyList()
    private var draftTracks: List<SoundscapeTrack> = emptyList()
    private var categoryName: String = "Composer"
    private var hasUnsavedChanges: Boolean = false
    private var showDiscardDialog: Boolean = false

    init {
        observeComposer()
        observeBackRequests(savedStateHandle)
    }

    constructor(
        categoryId: Long,
        repository: SoundscapeRepository,
    ) : this(
        savedStateHandle = SavedStateHandle(mapOf("categoryId" to categoryId)),
        repository = repository,
    )

    private fun observeComposer() {
        viewModelScope.launch {
            combine(
                repository.observeCategory(categoryId),
                repository.observeTracks(categoryId),
            ) { category, tracks ->
                category to tracks
            }.collect { (category, tracks) ->
                categoryName = category?.name ?: "Composer"
                persistedTracks = tracks.reindexed()
                if (!hasUnsavedChanges) {
                    draftTracks = persistedTracks
                }
                publishState()
            }
        }
    }

    private fun observeBackRequests(savedStateHandle: SavedStateHandle) {
        viewModelScope.launch {
            savedStateHandle.getStateFlow("composerBackRequestToken", 0L)
                .drop(1)
                .collect {
                    onBackRequested()
                }
        }
    }

    fun addImportedTrack(name: String, filePath: String) {
        val nextId = (draftTracks.maxOfOrNull { it.id } ?: 0L) + 1L
        draftTracks = (draftTracks + SoundscapeTrack(
            id = nextId,
            categoryId = categoryId,
            name = name,
            filePath = filePath,
            intensityLevel = IntensityLevel.I,
            mixVolumePercent = 100,
            displayOrder = draftTracks.size,
        )).reindexed()
        hasUnsavedChanges = true
        showDiscardDialog = false
        publishState()
    }

    fun updateTrackIntensity(trackId: Long, intensityLevel: IntensityLevel) {
        draftTracks = draftTracks.map { track ->
            if (track.id == trackId) track.copy(intensityLevel = intensityLevel) else track
        }
        hasUnsavedChanges = true
        publishState()
    }

    fun updateTrackMix(trackId: Long, mixVolumePercent: Int) {
        draftTracks = draftTracks.map { track ->
            if (track.id == trackId) track.copy(mixVolumePercent = mixVolumePercent.coerceIn(0, 100)) else track
        }
        hasUnsavedChanges = true
        publishState()
    }

    fun removeTrack(trackId: Long) {
        draftTracks = draftTracks.filterNot { it.id == trackId }.reindexed()
        hasUnsavedChanges = true
        publishState()
    }

    fun saveComposition() {
        viewModelScope.launch {
            val normalizedTracks = draftTracks.reindexed()
            runCatching {
                repository.saveTracks(categoryId, normalizedTracks)
            }.onSuccess {
                persistedTracks = normalizedTracks
                draftTracks = normalizedTracks
                hasUnsavedChanges = false
                showDiscardDialog = false
                publishState()
            }.onFailure { throwable ->
                _uiState.value = SoundscapeCategoryComposerUiState.Error(
                    throwable.message ?: "Unable to save composition.",
                )
            }
        }
    }

    fun onBackRequested() {
        viewModelScope.launch {
            if (hasUnsavedChanges) {
                showDiscardDialog = true
                publishState()
            } else {
                _navigationEvents.emit(SoundscapeCategoryComposerNavigation.NavigateBack)
            }
        }
    }

    fun dismissDiscardDialog() {
        showDiscardDialog = false
        publishState()
    }

    fun discardChangesAndNavigateBack() {
        draftTracks = persistedTracks
        hasUnsavedChanges = false
        showDiscardDialog = false
        publishState()
        viewModelScope.launch {
            _navigationEvents.emit(SoundscapeCategoryComposerNavigation.NavigateBack)
        }
    }

    private fun publishState() {
        _uiState.value = SoundscapeCategoryComposerUiState.Success(
            categoryName = categoryName,
            tracks = draftTracks,
            hasUnsavedChanges = hasUnsavedChanges,
            showDiscardChangesDialog = showDiscardDialog,
        )
    }
}

private fun List<SoundscapeTrack>.reindexed(): List<SoundscapeTrack> {
    return mapIndexed { index, track -> track.copy(displayOrder = index) }
}
