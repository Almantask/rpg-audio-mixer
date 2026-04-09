package com.example.rpgaudiomixer.infra.session

import androidx.room.*
import com.example.rpgaudiomixer.infra.scene.SceneEntity
import kotlinx.coroutines.flow.Flow

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
    indices = [
        Index(value = ["sessionId"]),
        Index(value = ["sceneId"])
    ]
)
data class SessionSceneCrossRef(
    val sessionId: Long,
    val sceneId: Long
)

@Dao
interface SessionSceneDao {
    @Transaction
    @Query("""
        SELECT scenes.* FROM scenes
        INNER JOIN session_scene_cross_ref ON scenes.id = session_scene_cross_ref.sceneId
        WHERE session_scene_cross_ref.sessionId = :sessionId
    """)
    fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun link(crossRef: SessionSceneCrossRef)

    @Delete
    fun unlink(crossRef: SessionSceneCrossRef)
}
