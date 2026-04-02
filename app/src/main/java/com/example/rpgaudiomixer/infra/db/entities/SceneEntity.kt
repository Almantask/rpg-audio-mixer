package com.example.rpgaudiomixer.infra.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val tags: String = "",            // comma-separated
    val masterAtmosphereVolume: Float = 0.8f,
    val masterSoundboardVolume: Float = 0.8f,
)
