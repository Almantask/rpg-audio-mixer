package com.example.rpgaudiomixer.data.scene

import androidx.room.Entity

@Entity(tableName = "session_scene_cross_ref", primaryKeys = ["sessionId", "sceneId"])
data class SessionSceneCrossRef(
    val sessionId: Long,
    val sceneId: Long,
)
