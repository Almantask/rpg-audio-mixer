package com.example.rpgaudiomixer.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    val tags: String? = null,
    val notes: String? = null,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long
)
