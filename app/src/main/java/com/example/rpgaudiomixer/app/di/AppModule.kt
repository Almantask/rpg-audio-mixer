package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.fx.FxRepositoryImpl
import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.trash.TrashRepositoryImpl
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.soundscape.SoundscapeRepositoryImpl
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.scene.SceneRepositoryImpl
import com.example.rpgaudiomixer.data.scene.SceneFxRepositoryImpl
import com.example.rpgaudiomixer.data.scene.SceneSoundscapeRepositoryImpl
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneFxDao
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.scene.SceneFxRepository
import com.example.rpgaudiomixer.domain.scene.SceneSoundscapeRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.trash.TrashRepository
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
    abstract fun bindSceneFxRepository(
        impl: SceneFxRepositoryImpl,
    ): SceneFxRepository

    @Binds
    @Singleton
    abstract fun bindSceneSoundscapeRepository(
        impl: SceneSoundscapeRepositoryImpl,
    ): SceneSoundscapeRepository

    @Binds
    @Singleton
    abstract fun bindSoundscapeRepository(
        impl: SoundscapeRepositoryImpl,
    ): SoundscapeRepository

    @Binds
    @Singleton
    abstract fun bindFxRepository(
        impl: FxRepositoryImpl,
    ): FxRepository

    @Binds
    @Singleton
    abstract fun bindTrashRepository(
        impl: TrashRepositoryImpl,
    ): TrashRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context,
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "rpg-audio-mixer.db",
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        fun provideCampaignDao(
            appDatabase: AppDatabase,
        ): CampaignDao {
            return appDatabase.campaignDao()
        }

        @Provides
        fun provideSessionDao(
            appDatabase: AppDatabase,
        ): SessionDao {
            return appDatabase.sessionDao()
        }

        @Provides
        fun provideSceneDao(
            appDatabase: AppDatabase,
        ): SceneDao {
            return appDatabase.sceneDao()
        }

        @Provides
        fun provideSceneFxDao(
            appDatabase: AppDatabase,
        ): SceneFxDao {
            return appDatabase.sceneFxDao()
        }

        @Provides
        fun provideSceneSoundscapeDao(
            appDatabase: AppDatabase,
        ): SceneSoundscapeDao {
            return appDatabase.sceneSoundscapeDao()
        }

        @Provides
        fun provideSessionSceneDao(
            appDatabase: AppDatabase,
        ): SessionSceneDao {
            return appDatabase.sessionSceneDao()
        }

        @Provides
        fun provideSoundscapeCategoryDao(
            appDatabase: AppDatabase,
        ): SoundscapeCategoryDao {
            return appDatabase.soundscapeCategoryDao()
        }

        @Provides
        fun provideSoundscapeTrackDao(
            appDatabase: AppDatabase,
        ): SoundscapeTrackDao {
            return appDatabase.soundscapeTrackDao()
        }

        @Provides
        fun provideFxTrackDao(
            appDatabase: AppDatabase,
        ): FxTrackDao {
            return appDatabase.fxTrackDao()
        }
    }
}
