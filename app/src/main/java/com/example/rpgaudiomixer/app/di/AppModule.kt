package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.app.data.audiotrack.AudioTrackRepositoryImpl
import com.example.rpgaudiomixer.app.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.app.data.local.AppDatabase
import com.example.rpgaudiomixer.app.data.local.dao.AudioTrackDao
import com.example.rpgaudiomixer.app.data.local.dao.CampaignDao
import com.example.rpgaudiomixer.app.data.local.dao.SessionDao
import com.example.rpgaudiomixer.app.data.session.SessionRepositoryImpl
import com.example.rpgaudiomixer.app.domain.repository.AudioTrackRepository
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
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
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun provideCampaignDao(db: AppDatabase): CampaignDao = db.campaignDao()

    @Provides
    fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideAudioTrackDao(db: AppDatabase): AudioTrackDao = db.audioTrackDao()
}

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindCampaignRepository(impl: CampaignRepositoryImpl): CampaignRepository

    @Binds
    @Singleton
    fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    fun bindAudioTrackRepository(impl: AudioTrackRepositoryImpl): AudioTrackRepository
}
