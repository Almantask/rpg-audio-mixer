package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.app.di.RepositoryModule
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.TrackStatsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * Replaces production repositories with fakes that acceptance tests can control per scenario
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class],
)
object FakeRepositoryModule {

    @Provides
    fun provideCampaignRepository(): CampaignRepository = PicoToHiltBridge.campaignRepository

    @Provides
    fun provideSceneRepository(): SceneRepository = PicoToHiltBridge.sceneRepository

    @Provides
    fun provideTrackStatsRepository(): TrackStatsRepository = PicoToHiltBridge.trackStatsRepository
}
