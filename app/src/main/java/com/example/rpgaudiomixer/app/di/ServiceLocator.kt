package com.example.rpgaudiomixer.app.di

import android.app.Application
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
    private var app: Application? = null

    fun init(app: Application) {
        this.app = app
    }

    @Volatile
    var mixedMusicPlayerFactory: () -> MixedMusicPlayer = {
        val application = requireNotNull(app) {
            "ServiceLocator.init(Application) must be called before using mixedMusicPlayerFactory"
        }
        MixedMusicPlayerImpl(
            trackFactory = ExoTrackFactory(application.applicationContext),
            trackRepository = LocalTrackRepository(application.applicationContext),
        )
    }
}
