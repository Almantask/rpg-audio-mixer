package com.example.rpgaudiomixer.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "rpg_audio_mixer.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideCampaignDao(db: AppDatabase) = db.campaignDao()
    @Provides fun provideSessionDao(db: AppDatabase) = db.sessionDao()
    @Provides fun provideSceneDao(db: AppDatabase) = db.sceneDao()
    @Provides fun provideSoundscapeCategoryDao(db: AppDatabase) = db.soundscapeCategoryDao()
    @Provides fun provideIntensityLevelDao(db: AppDatabase) = db.intensityLevelDao()
    @Provides fun provideTrackDao(db: AppDatabase) = db.trackDao()
    @Provides fun provideFXDao(db: AppDatabase) = db.fxDao()
    @Provides fun provideSessionScenesDao(db: AppDatabase) = db.sessionScenesDao()
}
