package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.trash.InMemoryCampaignTrashRepository
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.trash.CampaignTrashRepository
import com.example.rpgaudiomixer.ui.campaigns.CampaignPhotoPickerMode
import com.example.rpgaudiomixer.ui.campaigns.DefaultCampaignPhotoPickerMode
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
    abstract fun bindCampaignTrashRepository(
        impl: InMemoryCampaignTrashRepository,
    ): CampaignTrashRepository

    @Binds
    @Singleton
    abstract fun bindCampaignPhotoPickerMode(
        impl: DefaultCampaignPhotoPickerMode,
    ): CampaignPhotoPickerMode

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context,
        ): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "arcanum-audio.db",
        ).build()

        @Provides
        fun provideCampaignDao(
            appDatabase: AppDatabase,
        ): CampaignDao = appDatabase.campaignDao()
    }
}
