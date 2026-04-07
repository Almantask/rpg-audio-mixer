package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.repository.CampaignRepositoryImpl
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindCampaignRepository(
        impl: CampaignRepositoryImpl
    ): CampaignRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "arcanum_audio_db"
            ).build()
        }

        @Provides
        fun provideCampaignDao(database: AppDatabase): CampaignDao {
            return database.campaignDao()
        }
    }
}
