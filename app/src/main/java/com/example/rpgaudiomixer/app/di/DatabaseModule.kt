package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.domain.storage.CampaignRepository
import com.example.rpgaudiomixer.domain.storage.FxRepository
import com.example.rpgaudiomixer.domain.storage.SceneRepository
import com.example.rpgaudiomixer.domain.storage.SessionRepository
import com.example.rpgaudiomixer.domain.storage.SoundscapeRepository
import com.example.rpgaudiomixer.infra.storage.db.AppDatabase
import com.example.rpgaudiomixer.infra.storage.db.dao.CampaignDao
import com.example.rpgaudiomixer.infra.storage.db.dao.FxDao
import com.example.rpgaudiomixer.infra.storage.db.dao.SceneDao
import com.example.rpgaudiomixer.infra.storage.db.dao.SessionDao
import com.example.rpgaudiomixer.infra.storage.db.dao.SoundscapeDao
import com.example.rpgaudiomixer.infra.storage.repository.RoomCampaignRepository
import com.example.rpgaudiomixer.infra.storage.repository.RoomFxRepository
import com.example.rpgaudiomixer.infra.storage.repository.RoomSceneRepository
import com.example.rpgaudiomixer.infra.storage.repository.RoomSessionRepository
import com.example.rpgaudiomixer.infra.storage.repository.RoomSoundscapeRepository
import dagger.Binds
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "arcanum_audio.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideCampaignDao(db: AppDatabase): CampaignDao = db.campaignDao()
    @Provides fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
    @Provides fun provideSceneDao(db: AppDatabase): SceneDao = db.sceneDao()
    @Provides fun provideSoundscapeDao(db: AppDatabase): SoundscapeDao = db.soundscapeDao()
    @Provides fun provideFxDao(db: AppDatabase): FxDao = db.fxDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindCampaignRepository(impl: RoomCampaignRepository): CampaignRepository

    @Binds @Singleton
    abstract fun bindSessionRepository(impl: RoomSessionRepository): SessionRepository

    @Binds @Singleton
    abstract fun bindSceneRepository(impl: RoomSceneRepository): SceneRepository

    @Binds @Singleton
    abstract fun bindSoundscapeRepository(impl: RoomSoundscapeRepository): SoundscapeRepository

    @Binds @Singleton
    abstract fun bindFxRepository(impl: RoomFxRepository): FxRepository
}
