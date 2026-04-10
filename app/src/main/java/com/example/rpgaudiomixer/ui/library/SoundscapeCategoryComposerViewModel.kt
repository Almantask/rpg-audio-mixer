package com.example.rpgaudiomixer.ui.library

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

data class ComposerUiState(
    val category: SoundscapeCategory? = null,
    val tracks: List<SoundscapeTrack> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val hasUnsavedChanges: Boolean = false
)

@HiltViewModel
class SoundscapeCategoryComposerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val soundscapeRepository: SoundscapeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Long = savedStateHandle.get<String>("categoryId")?.toLongOrNull() ?: 0L

    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _hasUnsavedChanges = MutableStateFlow(false)

    val uiState: StateFlow<ComposerUiState> = combine(
        soundscapeRepository.observeCategoryById(categoryId),
        soundscapeRepository.observeTracksByCategory(categoryId),
        _errorMessage,
        _hasUnsavedChanges
    ) { category, tracks, error, unsaved ->
        ComposerUiState(
            category = category,
            tracks = tracks,
            isLoading = false,
            errorMessage = error,
            hasUnsavedChanges = unsaved
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ComposerUiState()
    )

    fun addTrack(name: String, fileUri: Uri, intensityLevel: IntensityLevel, mixVolume: Float) {
        viewModelScope.launch {
            try {
                val internalPath = copyAudioFileToInternalStorage(fileUri, name)
                val track = SoundscapeTrack(
                    categoryId = categoryId,
                    name = name,
                    filePath = internalPath,
                    intensityLevel = intensityLevel,
                    mixVolume = mixVolume
                )
                soundscapeRepository.createTrack(track)
                _hasUnsavedChanges.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add track: ${e.message}"
            }
        }
    }

    fun updateTrack(track: SoundscapeTrack) {
        viewModelScope.launch {
            try {
                soundscapeRepository.updateTrack(track)
                _hasUnsavedChanges.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update track: ${e.message}"
            }
        }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteTrack(trackId)
                _hasUnsavedChanges.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete track: ${e.message}"
            }
        }
    }

    fun updateTrackIntensity(trackId: Long, intensityLevel: IntensityLevel) {
        viewModelScope.launch {
            try {
                val track = soundscapeRepository.getTrackById(trackId)
                if (track != null) {
                    soundscapeRepository.updateTrack(track.copy(intensityLevel = intensityLevel))
                    _hasUnsavedChanges.value = true
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update intensity: ${e.message}"
            }
        }
    }

    fun updateTrackMixVolume(trackId: Long, mixVolume: Float) {
        viewModelScope.launch {
            try {
                val track = soundscapeRepository.getTrackById(trackId)
                if (track != null) {
                    soundscapeRepository.updateTrack(track.copy(mixVolume = mixVolume))
                    _hasUnsavedChanges.value = true
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update mix volume: ${e.message}"
            }
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            _hasUnsavedChanges.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun copyAudioFileToInternalStorage(uri: Uri, name: String): String {
        val audioDir = File(context.filesDir, "audio/soundscapes")
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }

        val extension = context.contentResolver.getType(uri)?.split("/")?.lastOrNull() ?: "mp3"
        val fileName = "${UUID.randomUUID()}_${name.replace(" ", "_")}.${extension}"
        val destinationFile = File(audioDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return destinationFile.absolutePath
    }
}
