package com.example.rpgaudiomixer.infra.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.infra.campaign.CampaignDao
import com.example.rpgaudiomixer.infra.campaign.CampaignEntity
import com.example.rpgaudiomixer.infra.session.SessionDao
import com.example.rpgaudiomixer.infra.session.SessionEntity
import com.example.rpgaudiomixer.infra.session.SessionSceneDao
import com.example.rpgaudiomixer.infra.session.SessionSceneCrossRef
import com.example.rpgaudiomixer.infra.scene.*
import com.example.rpgaudiomixer.infra.library.*

@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SessionSceneCrossRef::class,
        SoundscapeCategoryEntity::class,
        SoundscapeTrackEntity::class,
        FxTrackEntity::class,
        SceneSoundscapeCrossRef::class,
        SceneFxCrossRef::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun sessionSceneDao(): SessionSceneDao
    abstract fun soundscapeCategoryDao(): SoundscapeCategoryDao
    abstract fun soundscapeTrackDao(): SoundscapeTrackDao
    abstract fun fxTrackDao(): FxTrackDao
}
