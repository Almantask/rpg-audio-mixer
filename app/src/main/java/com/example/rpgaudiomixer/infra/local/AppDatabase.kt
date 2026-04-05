package com.example.rpgaudiomixer.infra.local

import androidx.room.*
import com.example.rpgaudiomixer.infra.local.dao.*
import com.example.rpgaudiomixer.infra.local.entities.*

@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SessionSceneCrossRef::class,
        SceneSoundscapeCrossRef::class,
        SoundscapeCategoryEntity::class,
        SoundscapeTrackEntity::class,
        FXTrackEntity::class,
        SceneFxCrossRef::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun sessionSceneDao(): SessionSceneDao
    abstract fun sceneSoundscapeDao(): SceneSoundscapeDao
    abstract fun soundscapeCategoryDao(): SoundscapeCategoryDao
    abstract fun soundscapeTrackDao(): SoundscapeTrackDao
    abstract fun fxDao(): FXDao
    abstract fun sceneFxDao(): SceneFxDao
}
