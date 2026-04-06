package com.example.rpgaudiomixer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.data.activescene.SceneAudioDao
import com.example.rpgaudiomixer.data.activescene.SceneFxCrossRef
import com.example.rpgaudiomixer.data.activescene.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.campaign.CampaignDao
import com.example.rpgaudiomixer.data.campaign.CampaignEntity
import com.example.rpgaudiomixer.data.fx.FxTrackDao
import com.example.rpgaudiomixer.data.fx.FxTrackEntity
import com.example.rpgaudiomixer.data.scene.SceneDao
import com.example.rpgaudiomixer.data.scene.SceneEntity
import com.example.rpgaudiomixer.data.scene.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.session.SessionDao
import com.example.rpgaudiomixer.data.session.SessionEntity
import com.example.rpgaudiomixer.data.soundscape.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.soundscape.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.soundscape.SoundscapeTrackEntity

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
        SceneFxCrossRef::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun soundscapeCategoryDao(): SoundscapeCategoryDao
    abstract fun soundscapeTrackDao(): SoundscapeTrackDao
    abstract fun fxTrackDao(): FxTrackDao
    abstract fun sceneAudioDao(): SceneAudioDao
}
