package com.example.rpgaudiomixer.domain.media

import kotlinx.coroutines.flow.StateFlow

/**
 * Contract for requesting sound playback.
 *
 * Note: In acceptance tests we inject a fake implementation and assert calls.
 */
interface MixedMusicPlayer {
    val previewState: StateFlow<PreviewPlaybackState>
    fun playSingleSound(soundId: String)
    fun playLoopingSound(categoryId: String)
    fun startPreview(queue: List<PreviewQueueItem>, startIndex: Int)
    fun togglePreviewPlayback()
    fun playNextPreview()
    fun playPreviousPreview()
    fun stopPreview()
}
