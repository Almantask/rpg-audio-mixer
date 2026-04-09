package com.example.rpgaudiomixer.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rpgaudiomixer.data.campaign.local.CampaignDao
import com.example.rpgaudiomixer.data.campaign.local.CampaignEntity
import com.example.rpgaudiomixer.data.scene.local.SceneDao
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionEntity
import com.example.rpgaudiomixer.data.session.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao

@Database(
    entities = [CampaignEntity::class, SessionEntity::class, SceneEntity::class, SessionSceneCrossRef::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sessionDao(): SessionDao
    abstract fun sceneDao(): SceneDao
    abstract fun sessionSceneDao(): SessionSceneDao
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
