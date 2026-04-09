package com.example.rpgaudiomixer.test.acceptance.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.app.di.AppModule
import com.example.rpgaudiomixer.data.campaign.CampaignRepositoryImpl
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.trash.InMemoryCampaignTrashRepository
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.trash.CampaignTrashRepository
import com.example.rpgaudiomixer.ui.campaigns.CampaignPhotoPickerMode
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
    abstract fun bindCampaignTrashRepository(
        impl: InMemoryCampaignTrashRepository,
    ): CampaignTrashRepository

    @Binds
    @Singleton
    abstract fun bindCampaignPhotoPickerMode(
        impl: FakeCampaignPhotoPickerMode,
    ): CampaignPhotoPickerMode

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
    }
}
