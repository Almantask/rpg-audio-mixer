package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.di.AcceptanceTestPlayerHolder.player
import java.util.concurrent.atomic.AtomicReference

/**
 * Simple mutable holder to bridge Cucumber's per-scenario world with Hilt's singleton graph.
 *
 * Each scenario sets [player] before launching the Activity.
 */
object AcceptanceTestPlayerHolder {

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

