package com.example.rpgaudiomixer.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.app.data.local.dao.AudioTrackDao
import com.example.rpgaudiomixer.app.data.local.dao.CampaignDao
import com.example.rpgaudiomixer.app.data.local.dao.SceneDao
import com.example.rpgaudiomixer.app.data.local.dao.SessionDao
import com.example.rpgaudiomixer.app.data.local.entities.AudioTrackEntity
import com.example.rpgaudiomixer.app.data.local.entities.CampaignEntity
import com.example.rpgaudiomixer.app.data.local.entities.SceneEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionSceneCrossRef
import com.example.rpgaudiomixer.app.data.local.entities.SoundscapeCategoryEntity

@Database(
    entities = [
        CampaignEntity::class,
        AudioTrackEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SoundscapeCategoryEntity::class,
        SessionSceneCrossRef::class,
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun audioTrackDao(): AudioTrackDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
}
