package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionScenesDao {
    @Transaction
    @Query("SELECT scenes.* FROM scenes INNER JOIN session_scene_cross_ref ON scenes.id = session_scene_cross_ref.sceneId WHERE session_scene_cross_ref.sessionId = :sessionId ORDER BY scenes.name ASC")
    fun observeBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun link(sessionScene: SessionSceneCrossRef)

    @Query("DELETE FROM session_scene_cross_ref WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun unlink(sessionId: Long, sceneId: Long)
}
