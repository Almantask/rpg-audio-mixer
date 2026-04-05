package com.example.rpgaudiomixer.infra.local.entities

import androidx.room.*
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

@Entity(tableName = "soundscape_categories")
data class SoundscapeCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconResId: Int?,
    val themeLabel: String?,
    val deletedAt: Long? = null
) {
    fun toDomain(trackCounts: Map<IntensityLevel, Int>) = SoundscapeCategory(
        id = id,
        name = name,
        iconResId = iconResId,
        themeLabel = themeLabel,
        trackCounts = trackCounts,
        deletedAt = deletedAt
    )
    
    companion object {
        fun fromDomain(category: SoundscapeCategory) = SoundscapeCategoryEntity(
            id = category.id,
            name = category.name,
            iconResId = category.iconResId,
            themeLabel = category.themeLabel,
            deletedAt = category.deletedAt
        )
    }
}

@Entity(
    tableName = "soundscape_tracks",
    foreignKeys = [
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class SoundscapeTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: Int, // 1 to 3
    val mixVolume: Float,
    val playCount: Int = 0
) {
    fun toDomain() = SoundscapeTrack(
        id = id,
        categoryId = categoryId,
        name = name,
        filePath = filePath,
        intensityLevel = when (intensityLevel) {
            1 -> IntensityLevel.I
            2 -> IntensityLevel.II
            3 -> IntensityLevel.III
            else -> IntensityLevel.I
        },
        mixVolume = mixVolume,
        playCount = playCount
    )
    
    companion object {
        fun fromDomain(track: SoundscapeTrack) = SoundscapeTrackEntity(
            id = track.id,
            categoryId = track.categoryId,
            name = track.name,
            filePath = track.filePath,
            intensityLevel = track.intensityLevel.value,
            mixVolume = track.mixVolume,
            playCount = track.playCount
        )
    }
}
