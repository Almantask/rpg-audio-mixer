package com.example.rpgaudiomixer.test.acceptance.world

import com.example.rpgaudiomixer.domain.media.MusicPlayer
import com.example.rpgaudiomixer.domain.media.SoundId
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Shared per-scenario world state.
 *
 * Created by Cucumber via PicoContainer and injected into step classes.
 */
class SoundboardWorld {

    val fakeMusicPlayer: FakeMusicPlayer = FakeMusicPlayer()

    fun reset() {
        fakeMusicPlayer.reset()
    }
}

class FakeMusicPlayer : MusicPlayer {

    data class PlayEvent(
        val soundId: SoundId,
        val startedAtNanos: Long,
    )

    private val _played = CopyOnWriteArrayList<SoundId>()
    val played: List<SoundId> get() = _played.toList()

    private val _playEvents = CopyOnWriteArrayList<PlayEvent>()
    val playEvents: List<PlayEvent> get() = _playEvents.toList()

    override fun play(soundId: SoundId) {
        _played += soundId
        _playEvents += PlayEvent(soundId = soundId, startedAtNanos = System.nanoTime())
    }

    fun reset() {
        _played.clear()
        _playEvents.clear()
    }
}
