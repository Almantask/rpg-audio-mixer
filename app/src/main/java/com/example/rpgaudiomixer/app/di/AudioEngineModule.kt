package com.example.rpgaudiomixer.app.di

import android.content.Context
import com.example.rpgaudiomixer.domain.audio.SceneAudioEngine
import com.example.rpgaudiomixer.domain.audio.SoundboardPlayer
import com.example.rpgaudiomixer.infra.media.ExoLoopableTrackPlayer
import com.example.rpgaudiomixer.infra.media.ExoOneTimeTrackPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioEngineModule {

    @Provides
    @Singleton
    fun provideSceneAudioEngine(@ApplicationContext context: Context): SceneAudioEngine =
        SceneAudioEngine(trackFactory = { filePath ->
            ExoLoopableTrackPlayer(track = filePath, appContext = context)
        })

    @Provides
    @Singleton
    fun provideSoundboardPlayer(@ApplicationContext context: Context): SoundboardPlayer =
        SoundboardPlayer(trackFactory = { filePath ->
            ExoOneTimeTrackPlayer(track = filePath, appContext = context)
        })
}
