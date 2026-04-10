package com.example.rpgaudiomixer.infra.library

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fx_tracks")
data class FxTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: String, // Comma-separated
    val durationMs: Long,
    val playCount: Int = 0,
    val deletedAt: Long? = null
)
