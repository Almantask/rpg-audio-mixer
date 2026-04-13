package com.example.rpgaudiomixer.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.app.data.local.dao.AudioTrackDao
import com.example.rpgaudiomixer.app.data.local.dao.CampaignDao
import com.example.rpgaudiomixer.app.data.local.dao.SessionDao
import com.example.rpgaudiomixer.app.data.local.entities.AudioTrackEntity
import com.example.rpgaudiomixer.app.data.local.entities.CampaignEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionEntity

@Database(
    entities = [CampaignEntity::class, AudioTrackEntity::class, SessionEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun audioTrackDao(): AudioTrackDao
    abstract fun sessionDao(): SessionDao
}
