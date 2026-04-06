package com.example.rpgaudiomixer.data.fx

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.FxTrack

@Entity(tableName = "fx_tracks")
data class FxTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: String = "",
    val durationMs: Long = 0L,
    val playCount: Int = 0,
)

fun FxTrackEntity.toDomain(): FxTrack = FxTrack(
    id = id,
    name = name,
    filePath = filePath,
    tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
    durationMs = durationMs,
    playCount = playCount,
)

fun FxTrack.toEntity(): FxTrackEntity = FxTrackEntity(
    id = id,
    name = name,
    filePath = filePath,
    tags = tags.joinToString(","),
    durationMs = durationMs,
    playCount = playCount,
)
