package com.example.rpgaudiomixer.test.acceptance.world

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Per-scenario test state managed by Cucumber's PicoContainer.
 *
 * PicoContainer:
 * 1. Constructs [FakeMusicPlayer] (detected via constructor)
 * 2. Injects it into this World
 * 3. Injects this World into step definition classes and [SoundboardComposeRule]
 *
 * The [fakeMusicPlayer] is then bridged to Hilt via [AcceptanceTestPlayerHolder].
 * This achieves pure dependency injection—no manual instantiation.
 */
class SoundboardWorld(
    val fakeMusicPlayer: FakeMusicPlayer,
) {

    /**
     * Resets the fake's state at the start of each scenario.
     *
     * Called by [SoundboardComposeRule] before the Activity launches.
     */
    fun reset() {
        fakeMusicPlayer.reset()
    }
}

/**
 * Pure fake implementation with no external dependencies.
 *
 * PicoContainer constructs this automatically when [SoundboardWorld] requests it.
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

    override fun playSingleSound(soundId: String) {
        _played += soundId
        _playEvents += PlayEvent(soundId = soundId, startedAtNanos = System.nanoTime())
    }

    override fun playLoopingSound(categoryId: String) {
        TODO("Not yet implemented")
    }

    fun reset() {
        _played.clear()
        _playEvents.clear()
    }
}
