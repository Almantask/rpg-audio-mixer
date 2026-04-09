package com.example.rpgaudiomixer.ui.soundscapes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PickedSoundscapeAudio(
    val categoryId: Long,
    val displayName: String,
    val filePath: String,
)

@Singleton
class SoundscapeAudioSelectionRepository @Inject constructor() {
    private val _selectedAudio = MutableStateFlow<PickedSoundscapeAudio?>(null)
    val selectedAudio: StateFlow<PickedSoundscapeAudio?> = _selectedAudio.asStateFlow()

    private val _isPickerOpen = MutableStateFlow(false)
    val isPickerOpen: StateFlow<Boolean> = _isPickerOpen.asStateFlow()

    private val _requestedMimeTypes = MutableStateFlow<List<String>>(emptyList())
    val requestedMimeTypes: StateFlow<List<String>> = _requestedMimeTypes.asStateFlow()

    private val _lastRequestedCategoryId = MutableStateFlow<Long?>(null)
    val lastRequestedCategoryId: StateFlow<Long?> = _lastRequestedCategoryId.asStateFlow()

    fun requestPicker(categoryId: Long) {
        _isPickerOpen.value = true
        _requestedMimeTypes.value = listOf("audio/*")
        _lastRequestedCategoryId.value = categoryId
    }

    fun submitSelection(categoryId: Long, displayName: String, filePath: String) {
        _selectedAudio.value = PickedSoundscapeAudio(
            categoryId = categoryId,
            displayName = displayName,
            filePath = filePath,
        )
        _isPickerOpen.value = false
    }

    fun submitSelectionForLastRequest(displayName: String, filePath: String) {
        val categoryId = _lastRequestedCategoryId.value ?: return
        submitSelection(categoryId, displayName, filePath)
    }

    fun consumeSelection() {
        _selectedAudio.value = null
    }

    fun closePicker() {
        _isPickerOpen.value = false
    }

    fun reset() {
        _selectedAudio.value = null
        _isPickerOpen.value = false
        _requestedMimeTypes.value = emptyList()
        _lastRequestedCategoryId.value = null
    }
}
