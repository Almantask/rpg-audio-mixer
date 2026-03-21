package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Pure fake implementation with no external dependencies.
 *
 * PicoContainer constructs this automatically and injects it into step definitions and rules.
 */
class FakeMusicPlayer : MixedMusicPlayer {

    data class PlayEvent(
        val soundId: String,
        val startedAtNanos: Long,
    )

    private val _played = CopyOnWriteArrayList<String>()
    val played: List<String> get() = _played.toList()

    private val _playEvents = CopyOnWriteArrayList<PlayEvent>()
    val playEvents: List<PlayEvent> get() = _playEvents.toList()

    private val _looped = CopyOnWriteArrayList<String>()
    val looped: List<String> get() = _looped.toList()

    override fun playSingleSound(soundId: String) {
        _played += soundId
        _playEvents += PlayEvent(soundId = soundId, startedAtNanos = System.nanoTime())
    }

    override fun playLoopingSound(categoryId: String) {
        _looped += categoryId
    }

    fun reset() {
        _played.clear()
        _playEvents.clear()
        _looped.clear()
    }
}