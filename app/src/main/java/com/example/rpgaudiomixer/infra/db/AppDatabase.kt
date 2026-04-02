package com.example.rpgaudiomixer.infra.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.infra.db.dao.CampaignDao
import com.example.rpgaudiomixer.infra.db.dao.LibraryDao
import com.example.rpgaudiomixer.infra.db.dao.SceneDao
import com.example.rpgaudiomixer.infra.db.dao.SessionDao
import com.example.rpgaudiomixer.infra.db.entities.CampaignEntity
import com.example.rpgaudiomixer.infra.db.entities.FXTrackEntity
import com.example.rpgaudiomixer.infra.db.entities.SceneEntity
import com.example.rpgaudiomixer.infra.db.entities.SceneFXTrackEntity
import com.example.rpgaudiomixer.infra.db.entities.SceneSoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.db.entities.SessionEntity
import com.example.rpgaudiomixer.infra.db.entities.SessionSceneEntity
import com.example.rpgaudiomixer.infra.db.entities.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.db.entities.TrackEntity

@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SessionSceneEntity::class,
        SoundscapeCategoryEntity::class,
        TrackEntity::class,
        FXTrackEntity::class,
        SceneSoundscapeCategoryEntity::class,
        SceneFXTrackEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun libraryDao(): LibraryDao
}
