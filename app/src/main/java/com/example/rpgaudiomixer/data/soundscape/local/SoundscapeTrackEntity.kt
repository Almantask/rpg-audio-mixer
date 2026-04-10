package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val intensityLevel: Int, // 1, 2, or 3 (I, II, III)
    val mixVolume: Float = 1.0f,
    val playCount: Int = 0
)
