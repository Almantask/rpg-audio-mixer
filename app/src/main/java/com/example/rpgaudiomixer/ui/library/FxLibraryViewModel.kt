package com.example.rpgaudiomixer.ui.library

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
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

data class FxLibraryUiState(
    val tracks: List<FxTrack> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fxRepository: FxRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<FxLibraryUiState> = combine(
        _searchQuery,
        _errorMessage
    ) { query, error ->
        val tracksFlow = if (query.isBlank()) {
            fxRepository.observeAll()
        } else {
            fxRepository.search(query)
        }

        FxLibraryUiState(
            tracks = tracksFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()).value,
            searchQuery = query,
            isLoading = false,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FxLibraryUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun importFx(name: String, fileUri: Uri, tags: List<String>) {
        viewModelScope.launch {
            try {
                val internalPath = copyAudioFileToInternalStorage(fileUri, name)
                val durationMs = getAudioDuration(internalPath)

                val track = FxTrack(
                    name = name,
                    filePath = internalPath,
                    tags = tags,
                    durationMs = durationMs
                )
                fxRepository.create(track)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to import FX: ${e.message}"
            }
        }
    }

    fun updateTrack(track: FxTrack) {
        viewModelScope.launch {
            try {
                fxRepository.update(track)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update track: ${e.message}"
            }
        }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            try {
                fxRepository.delete(trackId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete track: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun copyAudioFileToInternalStorage(uri: Uri, name: String): String {
        val audioDir = File(context.filesDir, "audio/fx")
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

    private fun getAudioDuration(filePath: String): Long {
        // Placeholder - would use MediaMetadataRetriever in production
        return 0L
    }
}
