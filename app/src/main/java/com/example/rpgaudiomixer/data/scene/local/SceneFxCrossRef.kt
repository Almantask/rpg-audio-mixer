package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.SceneFx

@Entity(
    tableName = "scene_fx_cross_refs",
    primaryKeys = ["sceneId", "fxTrackId"],
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FxTrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["fxTrackId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("sceneId"),
        Index("fxTrackId"),
    ],
)
data class SceneFxCrossRef(
    val sceneId: Long,
    val fxTrackId: Long,
    val displayOrder: Int,
)

data class SceneFxListItemEntity(
    val sceneId: Long,
    val fxTrackId: Long,
    val displayOrder: Int,
    val name: String,
    val filePath: String,
    val tags: String,
    val durationMs: Long,
    val playCount: Int,
)

fun SceneFxListItemEntity.asDomain(): SceneFx {
    return SceneFx(
        sceneId = sceneId,
        fxTrackId = fxTrackId,
        displayOrder = displayOrder,
        fxTrack = FxTrack(
            id = fxTrackId,
            name = name,
            filePath = filePath,
            tags = tags.split(",").map { tag -> tag.trim() }.filter { tag -> tag.isNotEmpty() },
            durationMs = durationMs,
            playCount = playCount,
        ),
    )
}
