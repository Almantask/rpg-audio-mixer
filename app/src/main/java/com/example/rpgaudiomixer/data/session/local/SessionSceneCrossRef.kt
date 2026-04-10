package com.example.rpgaudiomixer.data.session.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.rpgaudiomixer.data.scene.local.SceneEntity

@Entity(
    tableName = "session_scene_cross_ref",
    primaryKeys = ["sessionId", "sceneId"],
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("sceneId")]
)
data class SessionSceneCrossRef(
    val sessionId: Long,
    val sceneId: Long
)
