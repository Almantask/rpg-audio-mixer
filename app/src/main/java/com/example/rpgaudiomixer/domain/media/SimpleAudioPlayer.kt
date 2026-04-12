package com.example.rpgaudiomixer.domain.media

import android.net.Uri

/**
 * Simple audio player for single-URI preview playback.
 * Used by the Library screen to preview sounds before adding them to a scene.
 *
 * Lifecycle: ViewModel-scoped — the owning ViewModel creates and [release]s it.
 */
interface SimpleAudioPlayer {
    fun play(uri: Uri)
    fun pause()
    fun stop()
    fun release()
    val isPlaying: Boolean
    val currentUri: Uri?
}
