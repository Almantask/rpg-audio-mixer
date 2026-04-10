package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

@Entity(
    tableName = "soundscape_tracks",
    foreignKeys = [
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("categoryId")],
)
data class SoundscapeTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: Int,
    val mixVolume: Float,
)

fun SoundscapeTrackEntity.asDomain(): SoundscapeTrack = SoundscapeTrack(
    id = id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = IntensityLevel.fromValue(intensityLevel),
    mixVolume = mixVolume,
)
