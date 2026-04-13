package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.app.data.local.entities.SceneEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionSceneCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionSceneDao {
    @Query(
        "SELECT s.* FROM scenes s " +
            "INNER JOIN session_scene_cross_ref ref ON s.id = ref.sceneId " +
            "WHERE ref.sessionId = :sessionId AND s.isDeleted = 0 " +
            "ORDER BY ref.sortOrder ASC"
    )
    fun observeScenesForSession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun linkSceneToSession(ref: SessionSceneCrossRef)

    @Query("DELETE FROM session_scene_cross_ref WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long)

    @Query("DELETE FROM session_scene_cross_ref WHERE sessionId = :sessionId")
    suspend fun deleteAllForSession(sessionId: Long)
}
