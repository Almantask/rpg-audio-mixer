package com.example.rpgaudiomixer.data.fx.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.FxTrack

@Entity(tableName = "fx_tracks")
data class FxTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: String,
    val durationMs: Long,
    val playCount: Int,
    val deletedAt: Long? = null,
)

fun FxTrackEntity.asDomain(): FxTrack = FxTrack(
    id = id,
    name = name,
    filePath = filePath,
    tags = tags.split(",")
        .map { tag -> tag.trim() }
        .filter { tag -> tag.isNotEmpty() },
    durationMs = durationMs,
    playCount = playCount,
)

fun FxTrack.asEntity(): FxTrackEntity = FxTrackEntity(
    id = id,
    name = name,
    filePath = filePath,
    tags = tags.joinToString(separator = ","),
    durationMs = durationMs,
    playCount = playCount,
)
