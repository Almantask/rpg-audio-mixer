package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.app.di.RandomiserModule
import com.example.rpgaudiomixer.domain.media.Randomiser
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Replaces the prod randomiser with a fake that acceptance tests can control per scenario.
 * Everything else (MixedMusicPlayer, TrackFactory, TrackRepository) remains real.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RandomiserModule::class],
)
object FakeRandomiserModule {

    @Provides
    @Singleton
    fun provideRandomiser(): Randomiser = PicoToHiltBridge.randomiser
}
