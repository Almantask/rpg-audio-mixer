package com.example.rpgaudiomixer.app.di

import android.content.Context
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.scene.SceneRepositoryImpl
import com.example.rpgaudiomixer.data.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.data.soundscape.SoundscapeRepositoryImpl
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindCampaignRepository(
        impl: CampaignRepositoryImpl
    ): CampaignRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        impl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSceneRepository(
        impl: SceneRepositoryImpl
    ): SceneRepository

    @Binds
    @Singleton
    abstract fun bindSoundscapeRepository(
        impl: SoundscapeRepositoryImpl
    ): SoundscapeRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return AppDatabase.create(context)
        }

        @Provides
        @Singleton
        fun provideCampaignDao(database: AppDatabase) = database.campaignDao()

        @Provides
        @Singleton
        fun provideSessionDao(database: AppDatabase) = database.sessionDao()

        @Provides
        @Singleton
        fun provideSceneDao(database: AppDatabase) = database.sceneDao()

        @Provides
        @Singleton
        fun provideSessionSceneDao(database: AppDatabase) = database.sessionSceneDao()

        @Provides
        @Singleton
        fun provideSoundscapeCategoryDao(database: AppDatabase) = database.soundscapeCategoryDao()

        @Provides
        @Singleton
        fun provideSoundscapeTrackDao(database: AppDatabase) = database.soundscapeTrackDao()
    }
}

