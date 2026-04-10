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
        INNER JOIN session_scene_cross_refs ON scenes.id = session_scene_cross_refs.sceneId
        WHERE session_scene_cross_refs.sessionId = :sessionId
        ORDER BY scenes.id DESC
        """,
    )
    fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkCrossRef(crossRef: SessionSceneCrossRef)

    @Query("DELETE FROM session_scene_cross_refs WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun unlinkScene(sessionId: Long, sceneId: Long)

    suspend fun linkScene(sessionId: Long, sceneId: Long) {
        linkCrossRef(SessionSceneCrossRef(sessionId = sessionId, sceneId = sceneId))
    }
}
