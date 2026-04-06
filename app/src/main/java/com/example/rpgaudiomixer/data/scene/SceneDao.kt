package com.example.rpgaudiomixer.data.scene

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes ORDER BY name ASC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("""
        SELECT s.* FROM scenes s
        INNER JOIN session_scene_cross_ref r ON s.id = r.sceneId
        WHERE r.sessionId = :sessionId
        ORDER BY s.name ASC
    """)
    fun observeBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SceneEntity): Long

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun delete(id: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkToSession(crossRef: SessionSceneCrossRef)

    @Query("DELETE FROM session_scene_cross_ref WHERE sceneId = :sceneId AND sessionId = :sessionId")
    suspend fun unlinkFromSession(sceneId: Long, sessionId: Long)
}
