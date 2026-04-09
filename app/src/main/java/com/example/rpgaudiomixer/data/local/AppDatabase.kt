package com.example.rpgaudiomixer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Arcanum Audio Room Database.
 *
 * Version 5: Added SceneSoundscapeCrossRef entity and atmosphere_volume_percent to Scene.
 * Version 4: Added FxTrack entity with soft-delete support.
 * Version 3: Added Soundscape Category and Track entities.
 * Version 2: Added Session, Scene, and SessionSceneCrossRef entities.
 * Version 1: Initial schema with Campaign entity.
 */
@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SessionSceneCrossRef::class,
        SoundscapeCategoryEntity::class,
        SoundscapeTrackEntity::class,
        FxTrackEntity::class,
        SceneSoundscapeCrossRef::class
    ],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun sessionSceneDao(): SessionSceneDao
    abstract fun soundscapeCategoryDao(): SoundscapeCategoryDao
    abstract fun soundscapeTrackDao(): SoundscapeTrackDao
    abstract fun fxTrackDao(): FxTrackDao
    abstract fun sceneSoundscapeDao(): SceneSoundscapeDao
}
