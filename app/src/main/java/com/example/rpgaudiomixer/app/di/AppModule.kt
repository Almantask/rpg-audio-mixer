package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.app.data.audiotrack.AudioTrackRepositoryImpl
import com.example.rpgaudiomixer.app.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.app.data.local.AppDatabase
import com.example.rpgaudiomixer.app.data.local.dao.AudioTrackDao
import com.example.rpgaudiomixer.app.data.local.dao.CampaignDao
import com.example.rpgaudiomixer.app.data.local.dao.SceneDao
import com.example.rpgaudiomixer.app.data.local.dao.SessionDao
import com.example.rpgaudiomixer.app.data.local.dao.SoundscapeCategoryDao
import com.example.rpgaudiomixer.app.data.scene.SceneRepositoryImpl
import com.example.rpgaudiomixer.app.data.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.app.data.soundscapecategory.SoundscapeCategoryRepositoryImpl
import com.example.rpgaudiomixer.app.domain.repository.AudioTrackRepository
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import com.example.rpgaudiomixer.app.domain.repository.SoundscapeCategoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "rpg-audio-mixer.db"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    fun provideCampaignDao(db: AppDatabase): CampaignDao = db.campaignDao()

    @Provides
    fun provideAudioTrackDao(db: AppDatabase): AudioTrackDao = db.audioTrackDao()

    @Provides
    fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideSceneDao(db: AppDatabase): SceneDao = db.sceneDao()

    @Provides
    fun provideSoundscapeCategoryDao(db: AppDatabase): SoundscapeCategoryDao = db.soundscapeCategoryDao()
}

@Module
@InstallIn(SingletonComponent::class)
interface CampaignModule {
    @Binds
    @Singleton
    fun bindCampaignRepository(impl: CampaignRepositoryImpl): CampaignRepository
}

@Module
@InstallIn(SingletonComponent::class)
interface SessionModule {
    @Binds
    @Singleton
    fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
}

@Module
@InstallIn(SingletonComponent::class)
interface AudioTrackModule {
    @Binds
    @Singleton
    fun bindAudioTrackRepository(impl: AudioTrackRepositoryImpl): AudioTrackRepository
}

@Module
@InstallIn(SingletonComponent::class)
interface SceneModule {
    @Binds
    @Singleton
    fun bindSceneRepository(impl: SceneRepositoryImpl): SceneRepository
}

@Module
@InstallIn(SingletonComponent::class)
interface SoundscapeCategoryModule {
    @Binds
    @Singleton
    fun bindSoundscapeCategoryRepository(impl: SoundscapeCategoryRepositoryImpl): SoundscapeCategoryRepository
}
