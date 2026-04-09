package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionSceneDao {
    @Query(
        """
        SELECT scenes.*
        FROM scenes
        INNER JOIN session_scene_cross_ref
            ON scenes.id = session_scene_cross_ref.sceneId
        WHERE session_scene_cross_ref.sessionId = :sessionId
          AND scenes.deletedAt IS NULL
        ORDER BY scenes.id DESC
        """
    )
    fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Query(
        """
        SELECT *
        FROM scenes
        WHERE id NOT IN (
            SELECT sceneId
            FROM session_scene_cross_ref
            WHERE sessionId = :sessionId
        )
          AND deletedAt IS NULL
        ORDER BY id DESC
        """
    )
    fun observeAvailableScenesForSession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun link(crossRefs: List<SessionSceneCrossRef>)

    @Query(
        """
        DELETE FROM session_scene_cross_ref
        WHERE sessionId = :sessionId
        AND sceneId = :sceneId
        """
    )
    suspend fun unlink(sessionId: Long, sceneId: Long)
}
