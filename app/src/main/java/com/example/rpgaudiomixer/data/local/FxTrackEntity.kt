package com.example.rpgaudiomixer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fx_tracks")
data class FxTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val filePath: String,
    val tags: String,
    val durationMs: Long,
    val playCount: Int,
    val isDemo: Boolean,
    val isDeleted: Boolean,
)
