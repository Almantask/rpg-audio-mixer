package com.example.rpgaudiomixer.data.fx.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fx_tracks")
data class FxTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val filePath: String,
    val tagsCsv: String,
    val durationMs: Long,
    val playCount: Int,
)
