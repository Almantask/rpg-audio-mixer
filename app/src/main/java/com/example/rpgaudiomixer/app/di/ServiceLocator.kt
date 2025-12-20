package com.example.rpgaudiomixer.app.di

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayerImpl
import com.example.rpgaudiomixer.infra.media.ExoTrackFactory
import com.example.rpgaudiomixer.infra.storage.LocalTrackRepository

/**
 * Minimal service locator used to keep acceptance tests deterministic without introducing DI yet.
 *
 * In androidTest, you can replace [mixedMusicPlayerFactory] before launching the Activity.
 */
object ServiceLocator {

    @Volatile
    var mixedMusicPlayerFactory: () -> MixedMusicPlayer = { MixedMusicPlayerImpl(ExoTrackFactory(),
        LocalTrackRepository()) }
}

