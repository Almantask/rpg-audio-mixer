package com.example.rpgaudiomixer.ui.fx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.media.PreviewPlaybackState
import com.example.rpgaudiomixer.domain.media.PreviewQueueItem
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FxSortOption {
    NAME,
    DURATION,
    MOST_PLAYED,
}

data class FxLibraryContentState(
    val tracks: List<FxTrack>,
    val searchQuery: String,
    val selectedTag: String?,
    val availableTags: List<String>,
    val sortOption: FxSortOption,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val fxRepository: FxRepository,
    private val mixedMusicPlayer: MixedMusicPlayer,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedTag = MutableStateFlow<String?>(null)
    private val sortOption = MutableStateFlow(FxSortOption.NAME)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val previewState: StateFlow<PreviewPlaybackState> = mixedMusicPlayer.previewState

    val uiState: StateFlow<UiState<FxLibraryContentState>> = combine(
        searchQuery.flatMapLatest { query ->
            if (query.isBlank()) {
                fxRepository.observeAll()
            } else {
                fxRepository.search(query.trim())
            }
        },
        searchQuery,
        selectedTag,
        sortOption,
    ) { tracks, query, tag, sort ->
        val filteredTracks = tracks
            .filter { track -> tag == null || tag in track.tags }
            .sortedWith(sort.asComparator())
        UiState.Success(
            FxLibraryContentState(
                tracks = filteredTracks,
                searchQuery = query,
                selectedTag = tag,
                availableTags = tracks.flatMap { track -> track.tags }.distinct().sorted(),
                sortOption = sort,
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateSelectedTag(tag: String?) {
        selectedTag.value = if (selectedTag.value == tag) null else tag
    }

    fun updateSortOption(option: FxSortOption) {
        sortOption.value = option
    }

    fun importFxTrack(
        displayName: String,
        filePath: String,
        durationMs: Long,
    ) {
        viewModelScope.launch {
            runCatching {
                fxRepository.upsert(
                    FxTrack(
                        name = displayName,
                        filePath = filePath,
                        tags = emptyList(),
                        durationMs = durationMs,
                        playCount = 0,
                    ),
                )
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to import FX track."
            }
        }
    }

    fun saveTrackEdits(
        track: FxTrack,
        updatedName: String,
        updatedTagsText: String,
    ) {
        val trimmedName = updatedName.trim()
        if (trimmedName.isEmpty()) {
            _errorMessage.value = "FX name is required."
            return
        }

        viewModelScope.launch {
            runCatching {
                fxRepository.upsert(
                    track.copy(
                        name = trimmedName,
                        tags = updatedTagsText.toTagList(),
                    ),
                )
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to update FX track."
            }
        }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            runCatching {
                fxRepository.delete(trackId)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to delete FX track."
            }
        }
    }

    fun previewTrack(tracks: List<FxTrack>, trackId: Long) {
        val startIndex = tracks.indexOfFirst { track -> track.id == trackId }
        if (startIndex == -1) return

        mixedMusicPlayer.startPreview(
            queue = tracks.map { track ->
                PreviewQueueItem(
                    id = track.id,
                    title = track.name,
                    soundId = track.filePath,
                )
            },
            startIndex = startIndex,
        )
    }

    fun togglePreviewPlayback() {
        mixedMusicPlayer.togglePreviewPlayback()
    }

    fun playPreviousPreview() {
        mixedMusicPlayer.playPreviousPreview()
    }

    fun playNextPreview() {
        mixedMusicPlayer.playNextPreview()
    }

    fun stopPreview() {
        mixedMusicPlayer.stopPreview()
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    fun reportImportFailure(message: String) {
        _errorMessage.value = message
    }
}

private fun FxSortOption.asComparator(): Comparator<FxTrack> {
    return when (this) {
        FxSortOption.NAME -> compareBy { track -> track.name.lowercase() }
        FxSortOption.DURATION -> compareByDescending { track -> track.durationMs }
        FxSortOption.MOST_PLAYED -> compareByDescending<FxTrack> { track -> track.playCount }
            .thenBy { track -> track.name.lowercase() }
    }
}

private fun String.toTagList(): List<String> {
    return split(",")
        .map { tag -> tag.trim() }
        .filter { tag -> tag.isNotEmpty() }
        .distinct()
}
