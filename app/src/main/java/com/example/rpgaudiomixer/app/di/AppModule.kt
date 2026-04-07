package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.repository.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.repository.SessionRepositoryImpl
import com.example.rpgaudiomixer.data.repository.SceneRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
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
    abstract fun bindCampaignRepository(impl: CampaignRepositoryImpl): CampaignRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSceneRepository(impl: SceneRepositoryImpl): SceneRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "arcanum_audio_db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        @Singleton
        fun provideCampaignDao(database: AppDatabase): CampaignDao {
            return database.campaignDao()
        }

        @Provides
        @Singleton
        fun provideSessionDao(database: AppDatabase) = database.sessionDao()

        @Provides
        @Singleton
        fun provideSceneDao(database: AppDatabase) = database.sceneDao()

        @Provides
        @Singleton
        fun provideSessionSceneDao(database: AppDatabase) = database.sessionSceneDao()
    }
}

