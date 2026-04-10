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
        ORDER BY scenes.id DESC
        """,
    )
    fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Query("SELECT sceneId FROM session_scene_cross_refs WHERE sessionId = :sessionId")
    fun observeLinkedSceneIds(sessionId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun link(crossRef: SessionSceneCrossRef)

    @Query("DELETE FROM session_scene_cross_refs WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun unlink(sessionId: Long, sceneId: Long)
}
