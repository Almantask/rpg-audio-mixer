package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.infra.local.AppDatabase
import com.example.rpgaudiomixer.infra.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "arcanum_audio_db"
        ).fallbackToDestructiveMigration()
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
    fun provideSceneSoundscapeDao(database: AppDatabase): SceneSoundscapeDao {
        return database.sceneSoundscapeDao()
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
    fun provideFxDao(database: AppDatabase): FXDao {
        return database.fxDao()
    }

    @Provides
    fun provideSceneFxDao(database: AppDatabase): SceneFxDao {
        return database.sceneFxDao()
    }
}
