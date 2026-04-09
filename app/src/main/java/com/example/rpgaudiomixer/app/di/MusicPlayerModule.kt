package com.example.rpgaudiomixer.app.di

import android.content.Context
import com.example.rpgaudiomixer.domain.audio.SceneAudioEngine
import com.example.rpgaudiomixer.domain.audio.SoundboardPlayer
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayerImpl
import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.storage.TrackRepository
import com.example.rpgaudiomixer.infra.media.ExoTrackFactory
import com.example.rpgaudiomixer.infra.storage.LocalTrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object MusicPlayerModule {
    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }

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
        @ApplicationScope coroutineScope: CoroutineScope
    ): SceneAudioEngine = SceneAudioEngine(trackFactory, coroutineScope)

    @Provides
    @Singleton
    fun provideSoundboardPlayer(
        trackFactory: TrackFactory
    ): SoundboardPlayer = SoundboardPlayer(trackFactory)
}
