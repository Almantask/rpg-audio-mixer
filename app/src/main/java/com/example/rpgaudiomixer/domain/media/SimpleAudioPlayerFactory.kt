package com.example.rpgaudiomixer.domain.media

/**
 * Factory for creating [SimpleAudioPlayer] instances.
 *
 * ViewModel-scoped players should be created via this factory so that
 * production code uses the real ExoPlayer-backed implementation while
 * unit tests can substitute a mock.
 */
fun interface SimpleAudioPlayerFactory {
    fun create(): SimpleAudioPlayer
}
