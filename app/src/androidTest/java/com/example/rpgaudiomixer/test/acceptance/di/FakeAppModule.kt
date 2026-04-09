package com.example.rpgaudiomixer.test.acceptance.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.app.di.AppModule
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.fx.FxRepositoryImpl
import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.scene.SceneRepositoryImpl
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneFxDao
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.soundscape.SoundscapeRepositoryImpl
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.data.trash.InMemorySceneTrashRepository
import com.example.rpgaudiomixer.data.trash.InMemoryCampaignTrashRepository
import com.example.rpgaudiomixer.data.trash.InMemoryFxTrackTrashRepository
import com.example.rpgaudiomixer.data.trash.InMemorySoundscapeCategoryTrashRepository
import com.example.rpgaudiomixer.data.trash.InMemorySessionTrashRepository
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.trash.SceneTrashRepository
import com.example.rpgaudiomixer.domain.trash.CampaignTrashRepository
import com.example.rpgaudiomixer.domain.trash.FxTrackTrashRepository
import com.example.rpgaudiomixer.domain.trash.SoundscapeCategoryTrashRepository
import com.example.rpgaudiomixer.domain.trash.SessionTrashRepository
import com.example.rpgaudiomixer.ui.campaigns.CampaignPhotoPickerMode
import com.example.rpgaudiomixer.ui.fx.FxAudioPickerMode
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeAudioPickerMode
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class],
)
abstract class FakeAppModule {

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
    abstract fun bindFxRepository(
        impl: FxRepositoryImpl,
    ): FxRepository

    @Binds
    @Singleton
    abstract fun bindSoundscapeRepository(
        impl: SoundscapeRepositoryImpl,
    ): SoundscapeRepository

    @Binds
    @Singleton
    abstract fun bindCampaignTrashRepository(
        impl: InMemoryCampaignTrashRepository,
    ): CampaignTrashRepository

    @Binds
    @Singleton
    abstract fun bindSessionTrashRepository(
        impl: InMemorySessionTrashRepository,
    ): SessionTrashRepository

    @Binds
    @Singleton
    abstract fun bindSceneTrashRepository(
        impl: InMemorySceneTrashRepository,
    ): SceneTrashRepository

    @Binds
    @Singleton
    abstract fun bindFxTrackTrashRepository(
        impl: InMemoryFxTrackTrashRepository,
    ): FxTrackTrashRepository

    @Binds
    @Singleton
    abstract fun bindSoundscapeCategoryTrashRepository(
        impl: InMemorySoundscapeCategoryTrashRepository,
    ): SoundscapeCategoryTrashRepository

    @Binds
    @Singleton
    abstract fun bindCampaignPhotoPickerMode(
        impl: FakeCampaignPhotoPickerMode,
    ): CampaignPhotoPickerMode

    @Binds
    @Singleton
    abstract fun bindFxAudioPickerMode(
        impl: FakeFxAudioPickerMode,
    ): FxAudioPickerMode

    @Binds
    @Singleton
    abstract fun bindSoundscapeAudioPickerMode(
        impl: FakeSoundscapeAudioPickerMode,
    ): SoundscapeAudioPickerMode

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context,
        ): AppDatabase = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()

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
        fun provideSceneFxDao(
            appDatabase: AppDatabase,
        ): SceneFxDao = appDatabase.sceneFxDao()

        @Provides
        fun provideSceneSoundscapeDao(
            appDatabase: AppDatabase,
        ): SceneSoundscapeDao = appDatabase.sceneSoundscapeDao()

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

        @Provides
        fun provideFxTrackDao(
            appDatabase: AppDatabase,
        ): FxTrackDao = appDatabase.fxTrackDao()
    }
}
