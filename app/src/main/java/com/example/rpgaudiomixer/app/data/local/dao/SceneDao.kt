package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.app.data.local.entities.SceneEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionSceneCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT 1")
    fun observeLatest(): Flow<SceneEntity?>

    @Query("SELECT * FROM scenes WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SceneEntity>>

    @Query(
        "SELECT s.* FROM scenes s " +
            "INNER JOIN session_scene_cross_ref x ON s.id = x.sceneId " +
            "WHERE x.sessionId = :sessionId AND s.isDeleted = 0"
    )
    fun observeBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(scene: SceneEntity): Long

    @Query("UPDATE scenes SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE scenes SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun hardDelete(id: Long)

    @Query("DELETE FROM scenes WHERE isDeleted = 1 AND deletedAt < :cutoff")
    suspend fun purgeOlderThan(cutoff: Long)

    @Query("DELETE FROM scenes WHERE isDeleted = 1")
    suspend fun purgeAllDeleted()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun linkSceneToSession(crossRef: SessionSceneCrossRef)

    @Query("DELETE FROM session_scene_cross_ref WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long)

    @Query("DELETE FROM scenes")
    suspend fun deleteAll()

    @Query("DELETE FROM session_scene_cross_ref")
    suspend fun deleteAllCrossRefs()
}

