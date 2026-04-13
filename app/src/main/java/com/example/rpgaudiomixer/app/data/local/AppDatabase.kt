package com.example.rpgaudiomixer.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.app.data.local.dao.AudioTrackDao
import com.example.rpgaudiomixer.app.data.local.dao.CampaignDao
import com.example.rpgaudiomixer.app.data.local.dao.SceneDao
import com.example.rpgaudiomixer.app.data.local.dao.SessionDao
import com.example.rpgaudiomixer.app.data.local.dao.SessionSceneDao
import com.example.rpgaudiomixer.app.data.local.dao.SoundscapeCategoryDao
import com.example.rpgaudiomixer.app.data.local.entities.AudioTrackEntity
import com.example.rpgaudiomixer.app.data.local.entities.CampaignEntity
import com.example.rpgaudiomixer.app.data.local.entities.SceneEntity
import com.example.rpgaudiomixer.app.data.local.entities.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.app.data.local.entities.SessionEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionSceneCrossRef
import com.example.rpgaudiomixer.app.data.local.entities.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.app.data.local.entities.SoundscapeTrackCrossRef

@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        AudioTrackEntity::class,
        SceneEntity::class,
        SoundscapeCategoryEntity::class,
        SessionSceneCrossRef::class,
        SceneSoundscapeCrossRef::class,
        SoundscapeTrackCrossRef::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun audioTrackDao(): AudioTrackDao
    abstract fun sceneDao(): SceneDao
    abstract fun soundscapeCategoryDao(): SoundscapeCategoryDao
    abstract fun sessionSceneDao(): SessionSceneDao
}
