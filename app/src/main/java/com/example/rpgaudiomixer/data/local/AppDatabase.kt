package com.example.rpgaudiomixer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.TypeConverters

@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SoundscapeCategoryEntity::class,
        IntensityLevelEntity::class,
        TrackEntity::class,
        FXEntity::class,
        SessionSceneCrossRef::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun soundscapeCategoryDao(): SoundscapeCategoryDao
    abstract fun intensityLevelDao(): IntensityLevelDao
    abstract fun trackDao(): TrackDao
    abstract fun fxDao(): FXDao
    abstract fun sessionScenesDao(): SessionScenesDao
}
