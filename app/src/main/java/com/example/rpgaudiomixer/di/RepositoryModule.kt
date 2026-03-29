

package com.example.rpgaudiomixer.di

import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.data.repository.CampaignRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.data.repository.SessionRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.SoundscapeCategoryRepository
import com.example.rpgaudiomixer.data.repository.SoundscapeCategoryRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.FXRepository
import com.example.rpgaudiomixer.data.repository.FXRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.SessionScenesRepository
import com.example.rpgaudiomixer.data.repository.SessionScenesRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.data.repository.SceneRepositoryImpl
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
    abstract fun bindCampaignRepository(impl: CampaignRepositoryImpl): CampaignRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSoundscapeCategoryRepository(impl: SoundscapeCategoryRepositoryImpl): SoundscapeCategoryRepository

    @Binds
    @Singleton
    abstract fun bindFXRepository(impl: FXRepositoryImpl): FXRepository

    @Binds
    @Singleton
    abstract fun bindSessionScenesRepository(impl: SessionScenesRepositoryImpl): SessionScenesRepository

    @Binds
    @Singleton
    abstract fun bindSceneRepository(impl: SceneRepositoryImpl): SceneRepository
}
