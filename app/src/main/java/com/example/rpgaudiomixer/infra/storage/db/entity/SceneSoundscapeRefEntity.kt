package com.example.rpgaudiomixer.infra.storage.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/** Per-scene soundscape category slot — tracks which categories are active in a scene and their per-scene MIX volume. */
@Entity(
    tableName = "scene_soundscapes",
    primaryKeys = ["scene_id", "category_id"],
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["scene_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("scene_id"), Index("category_id")],
)
data class SceneSoundscapeRefEntity(
    @ColumnInfo(name = "scene_id") val sceneId: Long,
    @ColumnInfo(name = "category_id") val categoryId: Long,
    @ColumnInfo(name = "mix_volume") val mixVolume: Float = 0.5f,
    @ColumnInfo(name = "active_intensity") val activeIntensity: Int = 1,
    @ColumnInfo(name = "order_index") val orderIndex: Int = 0,
)
