package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

@Entity(tableName = "soundscape_categories")
data class SoundscapeCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconResId: Int?,
    val themeLabel: String?,
    val deletedAt: Long? = null,
)

data class SoundscapeCategoryListItemEntity(
    val id: Long,
    val name: String,
    val iconResId: Int?,
    val themeLabel: String?,
    val levelOneTrackCount: Int,
    val levelTwoTrackCount: Int,
    val levelThreeTrackCount: Int,
)

fun SoundscapeCategoryEntity.asDomain(): SoundscapeCategory = SoundscapeCategory(
    id = id,
    name = name,
    iconResId = iconResId,
    themeLabel = themeLabel,
)

fun SoundscapeCategoryListItemEntity.asDomain(): SoundscapeCategory = SoundscapeCategory(
    id = id,
    name = name,
    iconResId = iconResId,
    themeLabel = themeLabel,
    levelOneTrackCount = levelOneTrackCount,
    levelTwoTrackCount = levelTwoTrackCount,
    levelThreeTrackCount = levelThreeTrackCount,
)
