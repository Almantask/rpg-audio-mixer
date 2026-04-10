package com.example.rpgaudiomixer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity
import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.scene.local.SceneFxCrossRef
import com.example.rpgaudiomixer.data.scene.local.SceneFxDao
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackEntity
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionEntity
import com.example.rpgaudiomixer.data.session.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao

@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SessionSceneCrossRef::class,
        SceneFxCrossRef::class,
        SceneSoundscapeCrossRef::class,
        SoundscapeCategoryEntity::class,
        SoundscapeTrackEntity::class,
        FxTrackEntity::class,
    ],
    version = 8,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun sessionSceneDao(): SessionSceneDao
    abstract fun sceneFxDao(): SceneFxDao
    abstract fun sceneSoundscapeDao(): SceneSoundscapeDao
    abstract fun soundscapeCategoryDao(): SoundscapeCategoryDao
    abstract fun soundscapeTrackDao(): SoundscapeTrackDao
    abstract fun fxTrackDao(): FxTrackDao
}
