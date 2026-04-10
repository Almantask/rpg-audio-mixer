package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.Scene

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String?,
    val tags: String,
    val masterVolume: Float = 1f,
    val deletedAt: Long? = null,
)

fun SceneEntity.asDomain(): Scene = Scene(
    id = id,
    name = name,
    description = description,
    tags = tags.toTagList(),
    masterVolume = masterVolume,
)

private fun String.toTagList(): List<String> {
    return split(",")
        .map { tag -> tag.trim() }
        .filter { tag -> tag.isNotEmpty() }
        .distinct()
}
