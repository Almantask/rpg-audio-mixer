package com.example.rpgaudiomixer.infra.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scene_fx_tracks",
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FXTrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["fxTrackId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sceneId"), Index("fxTrackId")],
)
data class SceneFXTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val sceneId: Long,
    val fxTrackId: Long,
    val sortOrder: Int = 0,
)
