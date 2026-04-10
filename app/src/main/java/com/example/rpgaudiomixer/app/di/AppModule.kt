package com.example.rpgaudiomixer.app.di

import android.content.Context
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
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
        impl: CampaignRepositoryImpl
    ): CampaignRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return AppDatabase.create(context)
        }

        @Provides
        @Singleton
        fun provideCampaignDao(database: AppDatabase) = database.campaignDao()
    }
}

