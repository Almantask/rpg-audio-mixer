package com.example.rpgaudiomixer.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "scene_soundscape_cross_ref",
    primaryKeys = ["sceneId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sceneId"), Index("categoryId")],
)
data class SceneSoundscapeCrossRef(
    val sceneId: Long,
    val categoryId: Long,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: Int,
)
