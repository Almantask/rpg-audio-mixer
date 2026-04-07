package com.example.rpgaudiomixer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Arcanum Audio Room Database.
 *
 * Version 2: Added Session, Scene, and SessionSceneCrossRef entities.
 * Version 1: Initial schema with Campaign entity.
 */
@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SessionSceneCrossRef::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun sessionSceneDao(): SessionSceneDao
}
