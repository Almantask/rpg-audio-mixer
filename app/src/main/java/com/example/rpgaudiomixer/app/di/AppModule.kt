package com.example.rpgaudiomixer.app.di

import android.content.Context
import androidx.room.Room
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.LibraryRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import com.example.rpgaudiomixer.infra.db.AppDatabase
import com.example.rpgaudiomixer.infra.db.dao.CampaignDao
import com.example.rpgaudiomixer.infra.db.dao.LibraryDao
import com.example.rpgaudiomixer.infra.db.dao.SceneDao
import com.example.rpgaudiomixer.infra.db.dao.SessionDao
import com.example.rpgaudiomixer.infra.repository.RoomCampaignRepository
import com.example.rpgaudiomixer.infra.repository.RoomLibraryRepository
import com.example.rpgaudiomixer.infra.repository.RoomSceneRepository
import com.example.rpgaudiomixer.infra.repository.RoomSessionRepository
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "arcanum_audio.db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides fun provideCampaignDao(db: AppDatabase): CampaignDao = db.campaignDao()
    @Provides fun provideSessionDao(db: AppDatabase): SessionDao = db.sessionDao()
    @Provides fun provideSceneDao(db: AppDatabase): SceneDao = db.sceneDao()
    @Provides fun provideLibraryDao(db: AppDatabase): LibraryDao = db.libraryDao()

    @Provides
    @Singleton
    fun provideCampaignRepository(dao: CampaignDao): CampaignRepository =
        RoomCampaignRepository(dao)

    @Provides
    @Singleton
    fun provideSessionRepository(dao: SessionDao): SessionRepository =
        RoomSessionRepository(dao)

    @Provides
    @Singleton
    fun provideLibraryRepository(dao: LibraryDao): LibraryRepository =
        RoomLibraryRepository(dao)

    @Provides
    @Singleton
    fun provideSceneRepository(
        dao: SceneDao,
        libraryRepository: LibraryRepository,
    ): SceneRepository = RoomSceneRepository(dao, libraryRepository)
}

