package com.example.rpgaudiomixer.app.di

import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.FxRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.domain.repository.SessionSceneRepository
import com.example.rpgaudiomixer.domain.repository.TrackStatsRepository
import com.example.rpgaudiomixer.infra.repository.InMemoryCampaignRepository
import com.example.rpgaudiomixer.infra.repository.InMemoryFxRepository
import com.example.rpgaudiomixer.infra.repository.InMemorySceneRepository
import com.example.rpgaudiomixer.infra.repository.InMemorySessionRepository
import com.example.rpgaudiomixer.infra.repository.InMemorySessionSceneRepository
import com.example.rpgaudiomixer.infra.repository.InMemoryTrackStatsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCampaignRepository(): CampaignRepository {
        return InMemoryCampaignRepository()
    }

    @Provides
    @Singleton
    fun provideSessionRepository(): SessionRepository {
        return InMemorySessionRepository()
    }

    @Provides
    @Singleton
    fun provideSceneRepository(): SceneRepository {
        return InMemorySceneRepository()
    }

    @Provides
    @Singleton
    fun provideSessionSceneRepository(): SessionSceneRepository {
        return InMemorySessionSceneRepository()
    }

    @Provides
    @Singleton
    fun provideTrackStatsRepository(): TrackStatsRepository {
        return InMemoryTrackStatsRepository()
    }

    @Provides
    @Singleton
    fun provideFxRepository(): FxRepository {
        return InMemoryFxRepository()
    }
}
