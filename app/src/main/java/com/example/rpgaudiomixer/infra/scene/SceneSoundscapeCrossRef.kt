package com.example.rpgaudiomixer.infra.scene

import androidx.room.*
import com.example.rpgaudiomixer.infra.library.SoundscapeCategoryEntity

@Entity(
    tableName = "scene_soundscape_cross_ref",
    primaryKeys = ["sceneId", "categoryId"],
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
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class SceneSoundscapeCrossRef(
    val sceneId: Long,
    val categoryId: Long,
    val displayOrder: Int,
    val mixVolume: Float = 1.0f,
    val intensityLevel: Int = 1
)

data class SceneWithSoundscapes(
    @Embedded val scene: SceneEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SceneSoundscapeCrossRef::class,
            parentColumn = "sceneId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<SoundscapeCategoryEntity>
)
