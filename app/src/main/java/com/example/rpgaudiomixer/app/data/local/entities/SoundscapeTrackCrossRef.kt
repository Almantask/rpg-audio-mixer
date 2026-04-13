package com.example.rpgaudiomixer.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "soundscape_track_cross_ref",
    primaryKeys = ["soundscapeCategoryId", "audioTrackId"],
    foreignKeys = [
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["soundscapeCategoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AudioTrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["audioTrackId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("soundscapeCategoryId"), Index("audioTrackId")]
)
data class SoundscapeTrackCrossRef(
    val soundscapeCategoryId: Long,
    val audioTrackId: Long,
    val intensityLevel: Int
)
