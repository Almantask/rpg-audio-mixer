package com.example.rpgaudiomixer.app.di

import com.example.rpgaudiomixer.domain.media.Randomiser
import com.example.rpgaudiomixer.infra.media.KotlinRandomiser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RandomiserModule {
    @Provides
    @Singleton
    fun provideRandomiser(): Randomiser = KotlinRandomiser()
}
