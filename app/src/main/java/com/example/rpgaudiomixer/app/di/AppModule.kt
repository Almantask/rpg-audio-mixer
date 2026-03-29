package com.example.rpgaudiomixer.app.di

import com.example.rpgaudiomixer.domain.storage.GameRepository
import com.example.rpgaudiomixer.infra.storage.InMemoryGameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGameRepository(): GameRepository = InMemoryGameRepository()
}

