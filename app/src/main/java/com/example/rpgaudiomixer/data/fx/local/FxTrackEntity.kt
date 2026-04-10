package com.example.rpgaudiomixer.data.fx.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fx_tracks")
data class FxTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: String = "", // comma-separated
    val durationMs: Long = 0,
    val playCount: Int = 0
)
