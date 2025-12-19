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
    private val _played = CopyOnWriteArrayList<SoundId>()
    val played: List<SoundId> get() = _played.toList()

    override fun play(soundId: SoundId) {
        _played += soundId
    }

    fun reset() {
        _played.clear()
    }
}

