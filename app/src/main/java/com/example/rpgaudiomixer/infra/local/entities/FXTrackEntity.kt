package com.example.rpgaudiomixer.infra.local.entities

import androidx.room.*
import com.example.rpgaudiomixer.domain.model.FXTrack

@Entity(tableName = "fx_tracks")
data class FXTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: String, // Comma-separated
    val isOneShot: Boolean,
    val durationMs: Long = 0,
    val playCount: Int = 0,
    val deletedAt: Long? = null
) {
    fun toDomain() = FXTrack(
        id = id,
        name = name,
        filePath = filePath,
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        isOneShot = isOneShot,
        durationMs = durationMs,
        playCount = playCount,
        deletedAt = deletedAt
    )
    
    companion object {
        fun fromDomain(track: FXTrack) = FXTrackEntity(
            id = track.id,
            name = track.name,
            filePath = track.filePath,
            tags = track.tags.joinToString(","),
            isOneShot = track.isOneShot,
            durationMs = track.durationMs,
            playCount = track.playCount,
            deletedAt = track.deletedAt
        )
    }
}
