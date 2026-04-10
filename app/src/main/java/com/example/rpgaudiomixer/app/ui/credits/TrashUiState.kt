package com.example.rpgaudiomixer.app.ui.credits

data class TrashUiState(
    val items: List<TrashItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
