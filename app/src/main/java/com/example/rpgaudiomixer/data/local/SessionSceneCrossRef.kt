package com.example.rpgaudiomixer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Junction table linking sessions to scenes.
 *
 * A session can have multiple scenes, and a scene can be linked to multiple sessions.
 */
@Entity(
    tableName = "session_scene_cross_ref",
    primaryKeys = ["session_id", "scene_id"],
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SceneEntity::class,
            parentColumns = ["id"],
            childColumns = ["scene_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("session_id"), Index("scene_id")]
)
data class SessionSceneCrossRef(
    @ColumnInfo(name = "session_id")
    val sessionId: Long,

    @ColumnInfo(name = "scene_id")
    val sceneId: Long
)

/**
 * Data class for session with its linked scenes.
 */
data class SessionWithScenes(
    @Embedded val session: SessionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SessionSceneCrossRef::class,
            parentColumn = "session_id",
            entityColumn = "scene_id"
        )
    )
    val scenes: List<SceneEntity>
)

/**
 * DAO for SessionSceneCrossRef operations.
 */
@Dao
interface SessionSceneDao {

    /**
     * Observe all scenes linked to a specific session.
     */
    @Transaction
    @Query("""
        SELECT s.* FROM scenes s
        INNER JOIN session_scene_cross_ref sscr ON s.id = sscr.scene_id
        WHERE sscr.session_id = :sessionId
        ORDER BY s.name ASC
    """)
    fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    /**
     * Link a scene to a session.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun link(crossRef: SessionSceneCrossRef)

    /**
     * Unlink a scene from a session.
     */
    @Delete
    suspend fun unlink(crossRef: SessionSceneCrossRef)

    /**
     * Unlink all scenes from a session.
     */
    @Query("DELETE FROM session_scene_cross_ref WHERE session_id = :sessionId")
    suspend fun unlinkAllFromSession(sessionId: Long)
}
