package com.example.rpgaudiomixer.app.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.*
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoundscapeCategoryComposerViewModel @Inject constructor(
    private val repository: SoundscapeRepository
) : ViewModel() {

    private val _categoryId = MutableStateFlow<Long>(-1)
    private val _tracks = MutableStateFlow<List<SoundscapeTrack>>(emptyList())
    val tracks: StateFlow<List<SoundscapeTrack>> = _tracks.asStateFlow()

    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges: StateFlow<Boolean> = _hasUnsavedChanges.asStateFlow()

    fun setCategoryId(id: Long) {
        if (_categoryId.value == id) return
        _categoryId.value = id
        loadTracks(id)
    }

    private fun loadTracks(id: Long) {
        viewModelScope.launch {
            repository.observeTracksByCategory(id).collect {
                _tracks.value = it
                _hasUnsavedChanges.value = false
            }
        }
    }

    fun addTrack(name: String, filePath: String) {
        val newTrack = SoundscapeTrack(
            categoryId = _categoryId.value,
            name = name,
            filePath = filePath,
            intensityLevel = IntensityLevel.I
        )
        _tracks.value = _tracks.value + newTrack
        _hasUnsavedChanges.value = true
    }

    fun updateTrackIntensity(trackId: Long, level: IntensityLevel) {
        _tracks.value = _tracks.value.map {
            if (it.id == trackId) it.copy(intensityLevel = level) else it
        }
        _hasUnsavedChanges.value = true
    }

    fun updateTrackMix(trackId: Long, volume: Float) {
        _tracks.value = _tracks.value.map {
            if (it.id == trackId) it.copy(mixVolume = volume) else it
        }
        _hasUnsavedChanges.value = true
    }

    fun removeTrack(trackId: Long) {
        _tracks.value = _tracks.value.filter { it.id != trackId }
        _hasUnsavedChanges.value = true
    }

    fun saveChanges() {
        viewModelScope.launch {
            // First delete all existing tracks for this category? 
            // Or just upsert all?
            // Spec says "updates everywhere that category is used (no per-scene versioning)".
            // For simplicity, we just save each track. 
            // If they were removed from the list, we need to delete them from DB.
            val currentInDb = repository.observeTracksByCategory(_categoryId.value).first()
            val toDelete = currentInDb.filter { dbTrack -> 
                _tracks.value.none { it.id == dbTrack.id } 
            }
            
            toDelete.forEach { repository.deleteTrack(it.id) }
            _tracks.value.forEach { repository.upsertTrack(it) }
            _hasUnsavedChanges.value = false
        }
    }
}
