package com.example.rpgaudiomixer.infra.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** Join table: which scenes belong to which sessions. */
@Entity(
    tableName = "session_scenes",
    primaryKeys = ["session_id", "scene_id"],
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["scene_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("session_id"), Index("scene_id")],
)
data class SessionSceneCrossRef(
    @ColumnInfo(name = "session_id") val sessionId: Long,
    @ColumnInfo(name = "scene_id") val sceneId: Long,
    @ColumnInfo(name = "order_index") val orderIndex: Int = 0,
)
