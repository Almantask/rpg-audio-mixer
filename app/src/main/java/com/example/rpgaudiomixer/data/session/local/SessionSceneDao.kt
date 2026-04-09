package com.example.rpgaudiomixer.data.session.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionSceneDao {
    @Query(
        """
        SELECT scenes.*
        FROM scenes
        INNER JOIN session_scene_cross_refs
            ON scenes.id = session_scene_cross_refs.sceneId
        WHERE session_scene_cross_refs.sessionId = :sessionId
        ORDER BY scenes.name ASC, scenes.id ASC
        """,
    )
    fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkAll(crossRefs: List<SessionSceneCrossRef>)

    @Query("DELETE FROM session_scene_cross_refs WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun unlink(sessionId: Long, sceneId: Long)

    @Query("DELETE FROM session_scene_cross_refs")
    suspend fun clearAll()
}
