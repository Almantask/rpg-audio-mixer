package com.example.rpgaudiomixer.domain.media

data class PreviewQueueItem(
    val id: Long,
    val title: String,
    val soundId: String,
)

data class PreviewPlaybackState(
    val queue: List<PreviewQueueItem> = emptyList(),
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
) {
    val currentItem: PreviewQueueItem?
        get() = queue.getOrNull(currentIndex)

    val canSkipPrevious: Boolean
        get() = currentIndex > 0

    val canSkipNext: Boolean
        get() = currentIndex >= 0 && currentIndex < queue.lastIndex
}
