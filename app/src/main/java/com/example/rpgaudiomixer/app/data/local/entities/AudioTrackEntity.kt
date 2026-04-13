package com.example.rpgaudiomixer.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_tracks")
data class AudioTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val localPath: String,
    val originalUri: String,
    val type: String,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)
