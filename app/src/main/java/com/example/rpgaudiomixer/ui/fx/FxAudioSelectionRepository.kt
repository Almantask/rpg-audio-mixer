package com.example.rpgaudiomixer.ui.fx

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PickedFxAudio(
    val displayName: String,
    val filePath: String,
    val durationMs: Long,
    val isValidAudio: Boolean,
)

@Singleton
class FxAudioSelectionRepository @Inject constructor() {
    private val _selectedAudio = MutableStateFlow<PickedFxAudio?>(null)
    val selectedAudio: StateFlow<PickedFxAudio?> = _selectedAudio.asStateFlow()

    private val _isPickerOpen = MutableStateFlow(false)
    val isPickerOpen: StateFlow<Boolean> = _isPickerOpen.asStateFlow()

    private val _requestedMimeTypes = MutableStateFlow<List<String>>(emptyList())
    val requestedMimeTypes: StateFlow<List<String>> = _requestedMimeTypes.asStateFlow()

    fun requestPicker() {
        _requestedMimeTypes.value = listOf("audio/*")
        _isPickerOpen.value = true
    }

    fun submitSelection(
        displayName: String,
        filePath: String,
        durationMs: Long = 3_000L,
    ) {
        _selectedAudio.value = PickedFxAudio(
            displayName = displayName,
            filePath = filePath,
            durationMs = durationMs,
            isValidAudio = true,
        )
        _isPickerOpen.value = false
    }

    fun submitInvalidSelection(displayName: String, filePath: String) {
        _selectedAudio.value = PickedFxAudio(
            displayName = displayName,
            filePath = filePath,
            durationMs = 0L,
            isValidAudio = false,
        )
        _isPickerOpen.value = false
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
    }
}
