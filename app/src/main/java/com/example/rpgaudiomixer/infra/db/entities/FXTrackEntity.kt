package com.example.rpgaudiomixer.infra.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fx_tracks")
data class FXTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val filePath: String,
    val tags: String = "",            // comma-separated
    val playCount: Long = 0L,
)
