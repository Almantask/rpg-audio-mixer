package com.example.rpgaudiomixer.app.di

import com.example.rpgaudiomixer.domain.repository.*
import com.example.rpgaudiomixer.infra.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCampaignRepository(
        campaignRepositoryImpl: CampaignRepositoryImpl
    ): CampaignRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSceneRepository(
        sceneRepositoryImpl: SceneRepositoryImpl
    ): SceneRepository

    @Binds
    @Singleton
    abstract fun bindSoundscapeRepository(
        soundscapeRepositoryImpl: SoundscapeRepositoryImpl
    ): SoundscapeRepository

    @Binds
    @Singleton
    abstract fun bindFXRepository(
        fxRepositoryImpl: FXRepositoryImpl
    ): FXRepository
}

