package com.example.rpgaudiomixer.app.di

import android.content.Context
import com.example.rpgaudiomixer.domain.media.SimpleAudioPlayerFactory
import com.example.rpgaudiomixer.infra.media.ExoSimpleAudioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AudioPlayerModule {

    @Provides
    fun provideSimpleAudioPlayerFactory(
        @ApplicationContext context: Context,
    ): SimpleAudioPlayerFactory = SimpleAudioPlayerFactory {
        ExoSimpleAudioPlayer(context)
    }
}
