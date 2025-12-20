package com.example.rpgaudiomixer.test.acceptance.world

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
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

class FakeMusicPlayer : MixedMusicPlayer {

    data class PlayEvent(
        val soundId: SoundId,
        val startedAtNanos: Long,
    )

    private val _played = CopyOnWriteArrayList<SoundId>()
    val played: List<SoundId> get() = _played.toList()

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
