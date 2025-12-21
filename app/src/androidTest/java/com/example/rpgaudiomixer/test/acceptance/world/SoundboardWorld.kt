package com.example.rpgaudiomixer.test.acceptance.world

import com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer

/**
 * Per-scenario test state managed by Cucumber's PicoContainer.
 *
 * PicoContainer:
 * 1. Constructs [com.example.rpgaudiomixer.test.acceptance.fakes.FakeMusicPlayer] (detected via constructor)
 * 2. Injects it into this World
 * 3. Injects this World into step definition classes and [SoundboardComposeRule]
 *
 * The [fakeMusicPlayer] is then bridged to Hilt via [AcceptanceTestPlayerHolder].
 * This achieves pure dependency injection—no manual instantiation.
 */
class SoundboardWorld(
    val fakeMusicPlayer: FakeMusicPlayer,
) {
    fun reset() {
        fakeMusicPlayer.reset()
    }
}

