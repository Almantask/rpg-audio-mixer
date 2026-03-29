package com.example.rpgaudiomixer.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "session_scene_cross_ref",
    primaryKeys = ["sessionId", "sceneId"],
    foreignKeys = [
        ForeignKey(entity = SessionEntity::class, parentColumns = ["id"], childColumns = ["sessionId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = SceneEntity::class, parentColumns = ["id"], childColumns = ["sceneId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SessionSceneCrossRef(
    val sessionId: Long,
    val sceneId: Long
)
