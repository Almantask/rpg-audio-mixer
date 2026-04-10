package com.example.rpgaudiomixer.app.di

import android.content.Context
import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.media.*
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.storage.TrackRepository
import com.example.rpgaudiomixer.infra.media.ExoTrackFactory
import com.example.rpgaudiomixer.infra.storage.LocalTrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicPlayerModule {
    @Provides
    @Singleton
    fun provideTrackFactory(
        @ApplicationContext appContext: Context,
    ): TrackFactory = ExoTrackFactory(appContext)

    @Provides
    @Singleton
    fun provideTrackRepository(
        @ApplicationContext appContext: Context,
    ): TrackRepository = LocalTrackRepository(appContext)

    @Provides
    @Singleton
    fun provideMixedMusicPlayer(
        trackFactory: TrackFactory,
        trackRepository: TrackRepository,
    ): MixedMusicPlayer = MixedMusicPlayerImpl(
        trackFactory = trackFactory,
        trackRepository = trackRepository,
    )

    @Provides
    @Singleton
    fun provideSceneAudioEngine(
        trackFactory: TrackFactory,
        sceneRepository: SceneRepository,
        soundscapeRepository: SoundscapeRepository
    ): SceneAudioEngine = SceneAudioEngine(trackFactory, sceneRepository, soundscapeRepository)

    @Provides
    @Singleton
    fun provideSoundboardPlayer(
        trackFactory: TrackFactory,
        fxRepository: FxRepository
    ): SoundboardPlayer = SoundboardPlayer(trackFactory, fxRepository)
}
