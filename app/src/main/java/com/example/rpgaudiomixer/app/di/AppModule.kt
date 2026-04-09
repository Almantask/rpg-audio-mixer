package com.example.rpgaudiomixer.app.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.fx.AndroidFxAudioImporter
import com.example.rpgaudiomixer.data.fx.FxAudioImporter
import com.example.rpgaudiomixer.data.fx.FxRepositoryImpl
import com.example.rpgaudiomixer.data.local.FxTrackDao
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneFxDao
import com.example.rpgaudiomixer.data.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.scene.SceneRepositoryImpl
import com.example.rpgaudiomixer.data.settings.SettingsRepositoryImpl
import com.example.rpgaudiomixer.data.soundscape.AndroidImportedAudioStorage
import com.example.rpgaudiomixer.data.soundscape.ImportedAudioStorage
import com.example.rpgaudiomixer.data.soundscape.SoundscapeRepositoryImpl
import com.example.rpgaudiomixer.data.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.data.trash.TrashRepositoryImpl
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.settings.SettingsRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
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
    abstract fun bindSoundscapeRepository(
        impl: SoundscapeRepositoryImpl,
    ): SoundscapeRepository

    @Binds
    @Singleton
    abstract fun bindImportedAudioStorage(
        impl: AndroidImportedAudioStorage,
    ): ImportedAudioStorage

    @Binds
    @Singleton
    abstract fun bindFxRepository(
        impl: FxRepositoryImpl,
    ): FxRepository

    @Binds
    @Singleton
    abstract fun bindFxAudioImporter(
        impl: AndroidFxAudioImporter,
    ): FxAudioImporter

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl,
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindTrashRepository(
        impl: TrashRepositoryImpl,
    ): TrashRepository

    companion object {
        private const val SETTINGS_PREFS = "arcanum_settings"

        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext appContext: Context,
        ): AppDatabase = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "arcanum-audio.db",
        ).fallbackToDestructiveMigration().build()

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
        fun provideSceneSoundscapeDao(
            appDatabase: AppDatabase,
        ): SceneSoundscapeDao = appDatabase.sceneSoundscapeDao()

        @Provides
        fun provideSceneFxDao(
            appDatabase: AppDatabase,
        ): SceneFxDao = appDatabase.sceneFxDao()

        @Provides
        fun provideSoundscapeCategoryDao(
            appDatabase: AppDatabase,
        ): SoundscapeCategoryDao = appDatabase.soundscapeCategoryDao()

        @Provides
        fun provideSoundscapeTrackDao(
            appDatabase: AppDatabase,
        ): SoundscapeTrackDao = appDatabase.soundscapeTrackDao()

        @Provides
        fun provideFxTrackDao(
            appDatabase: AppDatabase,
        ): FxTrackDao = appDatabase.fxTrackDao()

        @Provides
        @Singleton
        fun provideSharedPreferences(
            @ApplicationContext appContext: Context,
        ): SharedPreferences = appContext.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
    }
}
