package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String?,
    val tagsCsv: String,
    val soundscapeCategoriesCsv: String,
    val soundboardEffectsCsv: String,
)
