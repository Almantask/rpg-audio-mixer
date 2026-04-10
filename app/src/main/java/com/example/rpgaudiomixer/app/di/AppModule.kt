package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.scene.SceneRepositoryImpl
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.soundscape.SoundscapeRepositoryImpl
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.Binds
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
        impl: CampaignRepositoryImpl,
    ): CampaignRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        impl: SessionRepositoryImpl,
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSceneRepository(
        impl: SceneRepositoryImpl,
    ): SceneRepository

    @Binds
    @Singleton
    abstract fun bindSoundscapeRepository(
        impl: SoundscapeRepositoryImpl,
    ): SoundscapeRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context,
        ): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "rpg-audio-mixer.db",
        ).fallbackToDestructiveMigration(dropAllTables = false).build()

        @Provides
        fun provideCampaignDao(
            appDatabase: AppDatabase,
        ): CampaignDao = appDatabase.campaignDao()

        @Provides
        fun provideSessionDao(
            appDatabase: AppDatabase,
        ): SessionDao = appDatabase.sessionDao()

        @Provides
        fun provideSceneDao(
            appDatabase: AppDatabase,
        ): SceneDao = appDatabase.sceneDao()

        @Provides
        fun provideSessionSceneDao(
            appDatabase: AppDatabase,
        ): SessionSceneDao = appDatabase.sessionSceneDao()

        @Provides
        fun provideSoundscapeCategoryDao(
            appDatabase: AppDatabase,
        ): SoundscapeCategoryDao = appDatabase.soundscapeCategoryDao()

        @Provides
        fun provideSoundscapeTrackDao(
            appDatabase: AppDatabase,
        ): SoundscapeTrackDao = appDatabase.soundscapeTrackDao()
    }
}
