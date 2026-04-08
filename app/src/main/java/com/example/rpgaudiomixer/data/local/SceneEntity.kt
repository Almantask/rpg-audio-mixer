package com.example.rpgaudiomixer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Scene entity for Room database
 * Represents a reusable location/moment (global, not scoped to a session)
 */
@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val tags: String = "" // Comma-separated tags
)
