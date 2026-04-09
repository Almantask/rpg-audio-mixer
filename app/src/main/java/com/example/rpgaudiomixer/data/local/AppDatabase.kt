package com.example.rpgaudiomixer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity

@Database(
    entities = [CampaignEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
}
