package com.example.rpgaudiomixer.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "scene_fx_cross_ref",
    primaryKeys = ["sceneId", "fxTrackId"],
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FxTrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["fxTrackId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sceneId"), Index("fxTrackId")],
)
data class SceneFxCrossRef(
    val sceneId: Long,
    val fxTrackId: Long,
    val displayOrder: Int,
)
