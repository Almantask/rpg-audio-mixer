package com.example.rpgaudiomixer.ui.fxlibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val fxRepository: FxRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FxLibraryUiState>(FxLibraryUiState.Loading)
    val uiState: StateFlow<FxLibraryUiState> = _uiState.asStateFlow()

    private val _showImportDialog = MutableStateFlow(false)
    val showImportDialog: StateFlow<Boolean> = _showImportDialog.asStateFlow()

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog.asStateFlow()

    private val _editingTrack = MutableStateFlow<FxTrack?>(null)
    val editingTrack: StateFlow<FxTrack?> = _editingTrack.asStateFlow()

    private val _isDownloadingDemo = MutableStateFlow(false)
    val isDownloadingDemo: StateFlow<Boolean> = _isDownloadingDemo.asStateFlow()

    init {
        loadFxTracks()
    }

    private fun loadFxTracks() {
        viewModelScope.launch {
            try {
                val tracks = fxRepository.getAllFxTracks()
                _uiState.value = FxLibraryUiState.Success(tracks)
            } catch (e: Exception) {
                _uiState.value = FxLibraryUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun importFxTrack(name: String, filePath: String, tags: List<String> = emptyList()) {
        viewModelScope.launch {
            try {
                fxRepository.importFxTrack(name, filePath, tags)
                loadFxTracks()
                hideImportDialog()
            } catch (e: Exception) {
                _uiState.value = FxLibraryUiState.Error(e.message ?: "Failed to import FX track")
            }
        }
    }

    fun updateFxTrack(track: FxTrack) {
        viewModelScope.launch {
            try {
                fxRepository.updateFxTrack(track)
                loadFxTracks()
                hideEditDialog()
            } catch (e: Exception) {
                _uiState.value = FxLibraryUiState.Error(e.message ?: "Failed to update FX track")
            }
        }
    }

    fun deleteFxTrack(id: String) {
        viewModelScope.launch {
            try {
                fxRepository.deleteFxTrack(id)
                loadFxTracks()
                hideEditDialog()
            } catch (e: Exception) {
                _uiState.value = FxLibraryUiState.Error(e.message ?: "Failed to delete FX track")
            }
        }
    }

    fun searchFxTracks(query: String) {
        viewModelScope.launch {
            try {
                val tracks = fxRepository.searchFxTracks(query)
                _uiState.value = FxLibraryUiState.Success(tracks)
            } catch (e: Exception) {
                _uiState.value = FxLibraryUiState.Error(e.message ?: "Failed to search FX tracks")
            }
        }
    }

    fun clearSearch() {
        loadFxTracks()
    }

    fun downloadDemoFxTracks() {
        viewModelScope.launch {
            try {
                _isDownloadingDemo.value = true
                // TODO: Implement demo download logic
                // For now, just reload tracks
                loadFxTracks()
            } catch (e: Exception) {
                _uiState.value = FxLibraryUiState.Error(e.message ?: "Failed to download demo tracks")
            } finally {
                _isDownloadingDemo.value = false
            }
        }
    }

    fun showImportDialog() {
        _showImportDialog.value = true
    }

    fun hideImportDialog() {
        _showImportDialog.value = false
    }

    fun showEditDialog(track: FxTrack) {
        _editingTrack.value = track
        _showEditDialog.value = true
    }

    fun hideEditDialog() {
        _showEditDialog.value = false
        _editingTrack.value = null
    }
}
