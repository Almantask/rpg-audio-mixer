package com.example.rpgaudiomixer.data.scene

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.Scene

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String? = null,
    val tags: String = "",
)

fun SceneEntity.toDomain(): Scene = Scene(
    id = id,
    name = name,
    description = description,
    tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() },
)

fun Scene.toEntity(): SceneEntity = SceneEntity(
    id = id,
    name = name,
    description = description,
    tags = tags.joinToString(","),
)
