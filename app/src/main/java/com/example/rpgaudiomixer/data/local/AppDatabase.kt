package com.example.rpgaudiomixer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionEntity
import com.example.rpgaudiomixer.data.session.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackEntity

@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SessionSceneCrossRef::class,
        SoundscapeCategoryEntity::class,
        SoundscapeTrackEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun sessionSceneDao(): SessionSceneDao
    abstract fun soundscapeCategoryDao(): SoundscapeCategoryDao
    abstract fun soundscapeTrackDao(): SoundscapeTrackDao

    companion object {
        private const val DATABASE_NAME = "arcanum_audio_db"

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration() // For development - removes all data on version change
            .build()
        }
    }
}
