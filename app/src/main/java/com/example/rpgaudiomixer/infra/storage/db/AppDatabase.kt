package com.example.rpgaudiomixer.infra.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rpgaudiomixer.infra.storage.db.dao.CampaignDao
import com.example.rpgaudiomixer.infra.storage.db.dao.FxDao
import com.example.rpgaudiomixer.infra.storage.db.dao.SceneDao
import com.example.rpgaudiomixer.infra.storage.db.dao.SessionDao
import com.example.rpgaudiomixer.infra.storage.db.dao.SoundscapeDao
import com.example.rpgaudiomixer.infra.storage.db.entity.CampaignEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.FxEffectEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SceneEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SceneFxRefEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SceneSoundscapeRefEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SessionEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SessionSceneCrossRef
import com.example.rpgaudiomixer.infra.storage.db.entity.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SoundscapeLayerEntity

@Database(
    entities = [
        CampaignEntity::class,
        SessionEntity::class,
        SceneEntity::class,
        SessionSceneCrossRef::class,
        SoundscapeCategoryEntity::class,
        SoundscapeLayerEntity::class,
        SceneSoundscapeRefEntity::class,
        FxEffectEntity::class,
        SceneFxRefEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun soundscapeDao(): SoundscapeDao
    abstract fun fxDao(): FxDao
}
