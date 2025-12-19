package com.example.rpgaudiomixer.app.di

import com.example.rpgaudiomixer.domain.media.MusicPlayer
import com.example.rpgaudiomixer.infra.media.AndroidMusicPlayer

/**
 * Minimal service locator used to keep acceptance tests deterministic without introducing DI yet.
 *
 * In androidTest, you can replace [musicPlayerFactory] before launching the Activity.
 */
object ServiceLocator {

    @Volatile
    var musicPlayerFactory: () -> MusicPlayer = { AndroidMusicPlayer() }
}

