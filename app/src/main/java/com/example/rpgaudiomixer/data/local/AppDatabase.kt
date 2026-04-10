package com.example.rpgaudiomixer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity

@Database(
    entities = [
        CampaignEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun campaignDao(): CampaignDao

    companion object {
        private const val DATABASE_NAME = "arcanum_audio_db"

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
