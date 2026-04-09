package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.app.di.MusicPlayerModule
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

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
    @Singleton
    fun provideMixedMusicPlayer(): MixedMusicPlayer = PicoToHiltBridge.player

    @Provides
    @Singleton
    fun provideTrackFactory(): TrackFactory = object : TrackFactory {
        override fun createLoopableTrackPlayer(track: String): TrackPlayer = NoOpTrackPlayer()

        override fun createOneTimeTrackPlayer(track: String): TrackPlayer = NoOpTrackPlayer()
    }

    @Provides
    @Singleton
    fun provideSceneAudioEngine(
        trackFactory: TrackFactory,
    ): SceneAudioEngine = SceneAudioEngine(trackFactory = trackFactory)

    @Provides
    @Singleton
    fun provideSoundboardPlayer(
        trackFactory: TrackFactory,
    ): SoundboardPlayer = SoundboardPlayer(trackFactory = trackFactory)
}

private class NoOpTrackPlayer : TrackPlayer {
    override val isPlaying: Boolean = false

    override fun play() = Unit

    override fun pause() = Unit

    override fun stop() = Unit

    override fun resume() = Unit

    override fun setVolume(volume: Float) = Unit

    override fun release() = Unit
}
