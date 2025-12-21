package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.app.di.MusicPlayerModule
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * Replaces the prod player with a fake that acceptance tests can control per scenario (using Hilt, for activity)
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MusicPlayerModule::class],
)
object FakeMixedMusicPlayerModule {

    @Provides
    fun provideMixedMusicPlayer(): MixedMusicPlayer = PicoToHiltBridge.player
}
