package com.example.rpgaudiomixer.infra.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** Per-scene FX effect slot on the soundboard. */
@Entity(
    tableName = "scene_fx",
    primaryKeys = ["scene_id", "fx_effect_id"],
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["scene_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FxEffectEntity::class,
            parentColumns = ["id"],
            childColumns = ["fx_effect_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("scene_id"), Index("fx_effect_id")],
)
data class SceneFxRefEntity(
    @ColumnInfo(name = "scene_id") val sceneId: Long,
    @ColumnInfo(name = "fx_effect_id") val fxEffectId: Long,
    @ColumnInfo(name = "order_index") val orderIndex: Int = 0,
)
