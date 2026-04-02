package com.example.rpgaudiomixer.infra.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scene_soundscape_categories",
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
data class SceneSoundscapeCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sceneId: Long,
    val categoryId: Long,
    val mixVolume: Float = 1.0f,
    val sortOrder: Int = 0,
)
