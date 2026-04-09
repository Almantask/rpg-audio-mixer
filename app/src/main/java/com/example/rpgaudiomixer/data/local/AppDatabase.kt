package com.example.rpgaudiomixer.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity
import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
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
        SceneSoundscapeCrossRef::class,
        SessionSceneCrossRef::class,
        SoundscapeCategoryEntity::class,
        SoundscapeTrackEntity::class,
        FxTrackEntity::class,
    ],
    version = 5,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun sceneSoundscapeDao(): SceneSoundscapeDao
    abstract fun sessionSceneDao(): SessionSceneDao
    abstract fun soundscapeCategoryDao(): SoundscapeCategoryDao
    abstract fun soundscapeTrackDao(): SoundscapeTrackDao
    abstract fun fxTrackDao(): FxTrackDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `sessions` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `campaignId` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `dateMillis` INTEGER NOT NULL,
                `coverArtUri` TEXT,
                FOREIGN KEY(`campaignId`) REFERENCES `campaigns`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_sessions_campaignId` ON `sessions` (`campaignId`)")
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `scenes` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT,
                `tagsCsv` TEXT NOT NULL,
                `soundscapeCategoriesCsv` TEXT NOT NULL
            )
            """.trimIndent(),
        )
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `session_scene_cross_refs` (
                `sessionId` INTEGER NOT NULL,
                `sceneId` INTEGER NOT NULL,
                PRIMARY KEY(`sessionId`, `sceneId`),
                FOREIGN KEY(`sessionId`) REFERENCES `sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`sceneId`) REFERENCES `scenes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_session_scene_cross_refs_sessionId` ON `session_scene_cross_refs` (`sessionId`)",
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_session_scene_cross_refs_sceneId` ON `session_scene_cross_refs` (`sceneId`)",
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `soundscape_categories` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `themeLabel` TEXT,
                `iconName` TEXT
            )
            """.trimIndent(),
        )
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `soundscape_tracks` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `categoryId` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `filePath` TEXT NOT NULL,
                `intensityLevel` INTEGER NOT NULL,
                `mixVolumePercent` INTEGER NOT NULL,
                FOREIGN KEY(`categoryId`) REFERENCES `soundscape_categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_soundscape_tracks_categoryId` ON `soundscape_tracks` (`categoryId`)")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `fx_tracks` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `filePath` TEXT NOT NULL,
                `tagsCsv` TEXT NOT NULL,
                `durationMs` INTEGER NOT NULL,
                `playCount` INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        database.execSQL(
            """
            ALTER TABLE `scenes`
            ADD COLUMN `soundboardEffectsCsv` TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `scene_soundscape_cross_refs` (
                `sceneId` INTEGER NOT NULL,
                `categoryId` INTEGER NOT NULL,
                `displayOrder` INTEGER NOT NULL,
                `mixVolumePercent` INTEGER NOT NULL,
                `intensityLevel` INTEGER NOT NULL,
                PRIMARY KEY(`sceneId`, `categoryId`),
                FOREIGN KEY(`sceneId`) REFERENCES `scenes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`categoryId`) REFERENCES `soundscape_categories`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_scene_soundscape_cross_refs_sceneId` ON `scene_soundscape_cross_refs` (`sceneId`)",
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_scene_soundscape_cross_refs_categoryId` ON `scene_soundscape_cross_refs` (`categoryId`)",
        )
    }
}
