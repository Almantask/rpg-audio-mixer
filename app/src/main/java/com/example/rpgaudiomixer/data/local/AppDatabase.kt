package com.example.rpgaudiomixer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Arcanum Audio Room Database.
 *
 * Version 1: Initial schema with Campaign entity.
 */
@Database(
    entities = [
        CampaignEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
}
