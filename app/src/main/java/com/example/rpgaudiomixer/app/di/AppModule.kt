package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.app.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.app.data.local.AppDatabase
import com.example.rpgaudiomixer.app.data.local.dao.CampaignDao
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
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
        ).build()
    }

    @Provides
    fun provideCampaignDao(db: AppDatabase): CampaignDao = db.campaignDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CampaignModule {
    @Binds
    @Singleton
    abstract fun bindCampaignRepository(impl: CampaignRepositoryImpl): CampaignRepository
}
