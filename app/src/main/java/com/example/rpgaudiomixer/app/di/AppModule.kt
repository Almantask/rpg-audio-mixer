package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.data.scene.SceneRepositoryImpl
import com.example.rpgaudiomixer.data.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
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

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "arcanum_audio_db"
            )
                .fallbackToDestructiveMigration() // For prototyping, allows schema changes
                .build()

        @Provides
        @Singleton
        fun provideCampaignDao(database: AppDatabase): CampaignDao =
            database.campaignDao()

        @Provides
        @Singleton
        fun provideSessionDao(database: AppDatabase): SessionDao =
            database.sessionDao()

        @Provides
        @Singleton
        fun provideSceneDao(database: AppDatabase): SceneDao =
            database.sceneDao()

        @Provides
        @Singleton
        fun provideSessionSceneDao(database: AppDatabase): SessionSceneDao =
            database.sessionSceneDao()
    }
}


