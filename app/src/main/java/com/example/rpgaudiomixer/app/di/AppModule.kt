package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.fx.FxRepositoryImpl
import com.example.rpgaudiomixer.data.local.*
import com.example.rpgaudiomixer.data.scene.SceneRepositoryImpl
import com.example.rpgaudiomixer.data.scenesoundscape.SceneSoundscapeRepositoryImpl
import com.example.rpgaudiomixer.data.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.data.soundscape.SoundscapeRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.*
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

    @Binds
    @Singleton
    abstract fun bindFxRepository(
        impl: FxRepositoryImpl
    ): FxRepository

    @Binds
    @Singleton
    abstract fun bindSceneSoundscapeRepository(
        impl: SceneSoundscapeRepositoryImpl
    ): SceneSoundscapeRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "arcanum_audio_database"
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
        fun provideSessionDao(database: AppDatabase): SessionDao {
            return database.sessionDao()
        }

        @Provides
        @Singleton
        fun provideSceneDao(database: AppDatabase): SceneDao {
            return database.sceneDao()
        }

        @Provides
        @Singleton
        fun provideSessionSceneDao(database: AppDatabase): SessionSceneDao {
            return database.sessionSceneDao()
        }

        @Provides
        @Singleton
        fun provideSoundscapeCategoryDao(database: AppDatabase): SoundscapeCategoryDao {
            return database.soundscapeCategoryDao()
        }

        @Provides
        @Singleton
        fun provideSoundscapeTrackDao(database: AppDatabase): SoundscapeTrackDao {
            return database.soundscapeTrackDao()
        }

        @Provides
        @Singleton
        fun provideFxTrackDao(database: AppDatabase): FxTrackDao {
            return database.fxTrackDao()
        }

        @Provides
        @Singleton
        fun provideSceneSoundscapeDao(database: AppDatabase): SceneSoundscapeDao {
            return database.sceneSoundscapeDao()
        }
    }
}

