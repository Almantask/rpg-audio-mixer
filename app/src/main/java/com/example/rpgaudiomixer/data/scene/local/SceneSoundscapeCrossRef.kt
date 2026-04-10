package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

@Entity(
    tableName = "scene_soundscape_cross_refs",
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
    indices = [
        Index("sceneId"),
        Index("categoryId"),
    ],
)
data class SceneSoundscapeCrossRef(
    val sceneId: Long,
    val categoryId: Long,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: Int,
)

data class SceneSoundscapeListItemEntity(
    val sceneId: Long,
    val categoryId: Long,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: Int,
    val name: String,
    val iconResId: Int?,
    val themeLabel: String?,
    val levelOneTrackCount: Int,
    val levelTwoTrackCount: Int,
    val levelThreeTrackCount: Int,
)

fun SceneSoundscapeListItemEntity.asDomain(): SceneSoundscape {
    return SceneSoundscape(
        sceneId = sceneId,
        categoryId = categoryId,
        displayOrder = displayOrder,
        mixVolume = mixVolume,
        intensityLevel = IntensityLevel.fromValue(intensityLevel),
        category = SoundscapeCategory(
            id = categoryId,
            name = name,
            iconResId = iconResId,
            themeLabel = themeLabel,
            levelOneTrackCount = levelOneTrackCount,
            levelTwoTrackCount = levelTwoTrackCount,
            levelThreeTrackCount = levelThreeTrackCount,
        ),
    )
}
