package com.example.rpgaudiomixer.infra.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = CampaignEntity::class,
            parentColumns = ["id"],
            childColumns = ["campaignId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("campaignId")]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val campaignId: Long,
    val name: String,
    val date: Long,
    val coverArtUri: String?,
    val lastOpenedSceneId: Long? = null,
    val deletedAt: Long? = null
) {
    fun toDomain() = Session(
        id = id,
        campaignId = campaignId,
        name = name,
        date = date,
        coverArtUri = coverArtUri,
        lastOpenedSceneId = lastOpenedSceneId,
        deletedAt = deletedAt
    )

    companion object {
        fun fromDomain(session: Session) = SessionEntity(
            id = session.id,
            campaignId = session.campaignId,
            name = session.name,
            date = session.date,
            coverArtUri = session.coverArtUri,
            lastOpenedSceneId = session.lastOpenedSceneId,
            deletedAt = session.deletedAt
        )
    }
}

@Entity(tableName = "scenes")
data class SceneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String?,
    val tags: String, // Comma-separated
    val deletedAt: Long? = null
) {
    fun toDomain() = Scene(
        id = id,
        name = name,
        description = description,
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        deletedAt = deletedAt
    )

    companion object {
        fun fromDomain(scene: Scene) = SceneEntity(
            id = scene.id,
            name = scene.name,
            description = scene.description,
            tags = scene.tags.joinToString(","),
            deletedAt = scene.deletedAt
        )
    }
}

@Entity(
    tableName = "session_scene_cross_ref",
    primaryKeys = ["sessionId", "sceneId"],
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("sceneId")]
)
data class SessionSceneCrossRef(
    val sessionId: Long,
    val sceneId: Long
)

@Entity(
    tableName = "scene_soundscape_cross_ref",
    primaryKeys = ["sceneId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["sceneId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SoundscapeCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sceneId"), Index("categoryId")]
)
data class SceneSoundscapeCrossRef(
    val sceneId: Long,
    val categoryId: Long,
    val displayOrder: Int,
    val mixVolume: Float = 0.8f,
    val intensityLevel: Int = 1 // 1=I, 2=II, 3=III
)

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
            entity = FXTrackEntity::class,
            parentColumns = ["id"],
            childColumns = ["fxTrackId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sceneId"), Index("fxTrackId")]
)
data class SceneFxCrossRef(
    val sceneId: Long,
    val fxTrackId: Long,
    val displayOrder: Int
)
