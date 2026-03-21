package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.domain.media.Randomiser
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
 * PicoContainer constructs FakeRandomiser
 *       ↓
 * PicoContainer injects into MainActivityComposeRule(fakeRandomiser)
 *       ↓
 * Rule sets: PicoToHiltBridge.randomiser = fakeRandomiser
 *       ↓
 * Hilt's FakeRandomiserModule reads from holder
 *       ↓
 * Activity receives the per-scenario fake randomiser (everything else is real)
 * ```
 *
 * No manual instantiation—PicoContainer manages the entire graph.
 */
object PicoToHiltBridge {

    private val ref: AtomicReference<Randomiser?> = AtomicReference(null)

    var randomiser: Randomiser
        get() = ref.get()
            ?: error(
                "PicoToHiltBridge.randomiser was not set. " +
                    "Make sure your scenario sets it before launching the Activity."
            )
        set(value) {
            ref.set(value)
        }
}

