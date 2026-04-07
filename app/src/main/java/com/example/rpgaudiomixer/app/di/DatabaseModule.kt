package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.data.local.*
import com.example.rpgaudiomixer.data.repository.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.repository.SceneRepositoryImpl
import com.example.rpgaudiomixer.data.repository.SessionRepositoryImpl
import com.example.rpgaudiomixer.data.repository.SoundscapeRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

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
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "arcanum_audio_db"
            )
                .fallbackToDestructiveMigration() // For development; remove for production
                .build()
        }

        @Provides
        fun provideCampaignDao(database: AppDatabase): CampaignDao {
            return database.campaignDao()
        }

        @Provides
        fun provideSessionDao(database: AppDatabase): SessionDao {
            return database.sessionDao()
        }

        @Provides
        fun provideSceneDao(database: AppDatabase): SceneDao {
            return database.sceneDao()
        }

        @Provides
        fun provideSessionSceneDao(database: AppDatabase): SessionSceneDao {
            return database.sessionSceneDao()
        }

        @Provides
        fun provideSoundscapeCategoryDao(database: AppDatabase): SoundscapeCategoryDao {
            return database.soundscapeCategoryDao()
        }

        @Provides
        fun provideSoundscapeTrackDao(database: AppDatabase): SoundscapeTrackDao {
            return database.soundscapeTrackDao()
        }
    }
}
