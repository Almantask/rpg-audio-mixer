package com.example.rpgaudiomixer.app.di

import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.infra.media.ExoTrackFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MusicPlayerModule {

    @Binds
    @Singleton
    abstract fun bindTrackFactory(
        impl: ExoTrackFactory
    ): TrackFactory

    companion object {
        @Provides
        @Singleton
        fun provideSceneAudioEngine(
            trackFactory: TrackFactory
        ): SceneAudioEngine {
            return SceneAudioEngine(trackFactory)
        }

        @Provides
        @Singleton
        fun provideSoundboardPlayer(
            trackFactory: TrackFactory
        ): SoundboardPlayer {
            return SoundboardPlayer(trackFactory)
        }
    }
}
