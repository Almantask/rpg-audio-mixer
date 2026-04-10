package com.example.rpgaudiomixer.data.session.local

import androidx.room.*
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionSceneDao {

    @Query("""
        SELECT scenes.* FROM scenes
        INNER JOIN session_scene_cross_ref ON scenes.id = session_scene_cross_ref.sceneId
        WHERE session_scene_cross_ref.sessionId = :sessionId
        ORDER BY scenes.name ASC
    """)
    fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun link(crossRef: SessionSceneCrossRef)

    @Delete
    suspend fun unlink(crossRef: SessionSceneCrossRef)

    @Query("DELETE FROM session_scene_cross_ref WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun unlinkByIds(sessionId: Long, sceneId: Long)
}
