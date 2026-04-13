package com.example.rpgaudiomixer.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "scene_soundscape_cross_ref",
    primaryKeys = ["sceneId", "soundscapeCategoryId"],
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["soundscapeCategoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sceneId"), Index("soundscapeCategoryId")]
)
data class SceneSoundscapeCrossRef(
    val sceneId: Long,
    val soundscapeCategoryId: Long,
    val sortOrder: Int = 0,
    val mixVolume: Float = 0.75f
)
