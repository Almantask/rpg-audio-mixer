package com.example.rpgaudiomixer.data.soundscape

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

@Entity(tableName = "soundscape_categories")
data class SoundscapeCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)

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
    val intensityLevel: Int = 1,
    val mixVolume: Float = 1.0f,
    val playCount: Int = 0,
)

fun SoundscapeTrackEntity.toDomain(): SoundscapeTrack = SoundscapeTrack(
    id = id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = IntensityLevel.entries.first { it.value == intensityLevel },
    mixVolume = mixVolume,
)

fun SoundscapeTrack.toEntity(): SoundscapeTrackEntity = SoundscapeTrackEntity(
    id = id,
    categoryId = categoryId,
    name = name,
    filePath = filePath,
    intensityLevel = intensityLevel.value,
    mixVolume = mixVolume,
)

fun SoundscapeCategoryEntity.toDomain(tracks: List<SoundscapeTrack> = emptyList()): SoundscapeCategory =
    SoundscapeCategory(id = id, name = name, tracks = tracks)
