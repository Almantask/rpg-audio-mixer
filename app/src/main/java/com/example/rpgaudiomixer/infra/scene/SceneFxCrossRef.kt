package com.example.rpgaudiomixer.infra.scene

import androidx.room.*
import com.example.rpgaudiomixer.infra.library.FxTrackEntity

@Entity(
    tableName = "scene_fx_cross_ref",
    primaryKeys = ["sceneId", "fxTrackId"],
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FxTrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["fxTrackId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fxTrackId")]
)
data class SceneFxCrossRef(
    val sceneId: Long,
    val fxTrackId: Long,
    val displayOrder: Int
)

data class SceneWithFx(
    @Embedded val scene: SceneEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SceneFxCrossRef::class,
            parentColumn = "sceneId",
            entityColumn = "fxTrackId"
        )
    )
    val fxTracks: List<FxTrackEntity>
)
