package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Session-Scene relationship operations
 */
@Dao
interface SessionSceneDao {
    /**
     * Observe scenes linked to a specific session
     */
    @Transaction
    @Query("""
        SELECT scenes.* FROM scenes
        INNER JOIN session_scene_cross_ref ON scenes.id = session_scene_cross_ref.sceneId
        WHERE session_scene_cross_ref.sessionId = :sessionId
        ORDER BY scenes.name ASC
    """)
    fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    /**
     * Link a scene to a session
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun link(crossRef: SessionSceneCrossRef)

    /**
     * Unlink a scene from a session
     */
    @Query("DELETE FROM session_scene_cross_ref WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun unlink(sessionId: Long, sceneId: Long)

    /**
     * Check if a scene is linked to a session
     */
    @Query("SELECT COUNT(*) FROM session_scene_cross_ref WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun isSceneLinked(sessionId: Long, sceneId: Long): Int
}
