package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity

@Entity(
    tableName = "scene_fx_cross_refs",
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
