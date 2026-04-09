package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.infra.campaign.CampaignDao
import com.example.rpgaudiomixer.infra.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.infra.session.SessionDao
import com.example.rpgaudiomixer.infra.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.infra.session.SessionSceneDao
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.infra.scene.SceneDao
import com.example.rpgaudiomixer.infra.scene.SceneRepositoryImpl
import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.infra.library.FxTrackDao
import com.example.rpgaudiomixer.infra.library.FxRepositoryImpl
import com.example.rpgaudiomixer.infra.library.SoundscapeCategoryDao
import com.example.rpgaudiomixer.infra.library.SoundscapeRepositoryImpl
import com.example.rpgaudiomixer.infra.library.SoundscapeTrackDao
import com.example.rpgaudiomixer.infra.local.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindCampaignRepository(impl: CampaignRepositoryImpl): CampaignRepository
 
    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
 
    @Binds
    @Singleton
    abstract fun bindSceneRepository(impl: SceneRepositoryImpl): SceneRepository
 
    @Binds
    @Singleton
    abstract fun bindSoundscapeRepository(impl: SoundscapeRepositoryImpl): SoundscapeRepository

    @Binds
    @Singleton
    abstract fun bindFxRepository(impl: FxRepositoryImpl): FxRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "arcanum_audio.db"
            ).build()
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
 
        @Provides
        fun provideFxTrackDao(database: AppDatabase): FxTrackDao {
            return database.fxTrackDao()
        }
    }
}
