package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer

/**
 * Simple mutable holder to bridge Cucumber's per-scenario world with Hilt's singleton graph.
 *
 * Each scenario sets [player] before launching the Activity.
 */
object AcceptanceTestPlayerHolder {

    @Volatile
    var player: MixedMusicPlayer = object : MixedMusicPlayer {
        override fun playSingleSound(soundId: String) =
            error("AcceptanceTestPlayerHolder.player wasn't set for this scenario")

        override fun playLoopingSound(categoryId: String) =
            error("AcceptanceTestPlayerHolder.player wasn't set for this scenario")
    }
}

