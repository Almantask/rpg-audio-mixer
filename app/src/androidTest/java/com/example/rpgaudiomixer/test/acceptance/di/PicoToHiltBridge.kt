package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import java.util.concurrent.atomic.AtomicReference

/**
 * Global bridge between PicoContainer's per-scenario lifecycle and Hilt's singleton graph.
 *
 * ## Why this exists
 * - **Hilt**: Creates SingletonComponent once per test class (expensive to recreate)
 * - **Cucumber + PicoContainer**: Creates fresh instances per scenario via pure DI
 * - **This holder**: Allows scenarios to swap fakes without restarting Hilt
 *
 * ## Pure DI Flow
 * ```
 * PicoContainer constructs FakeMusicPlayer
 *       ↓
 * PicoContainer injects into SoundboardWorld(fakeMusicPlayer)
 *       ↓
 * PicoContainer injects World into SoundboardComposeRule(world)
 *       ↓
 * Rule sets: AcceptanceTestPlayerHolder.player = world.fakeMusicPlayer
 *       ↓
 * Hilt's FakeMixedMusicPlayerModule reads from holder
 *       ↓
 * Activity receives the per-scenario fake
 * ```
 *
 * No manual instantiation—PicoContainer manages the entire graph.
 */
object PicoToHiltBridge {

    private val ref: AtomicReference<MixedMusicPlayer?> = AtomicReference(null)

    var player: MixedMusicPlayer
        get() = ref.get()
            ?: error(
                "AcceptanceTestPlayerHolder.player was not set. " +
                    "Make sure your scenario sets it before launching the Activity."
            )
        set(value) {
            ref.set(value)
        }
}

