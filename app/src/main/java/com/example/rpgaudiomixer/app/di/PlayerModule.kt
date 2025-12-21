package com.example.rpgaudiomixer.app.di

import android.content.Context
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
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
}
