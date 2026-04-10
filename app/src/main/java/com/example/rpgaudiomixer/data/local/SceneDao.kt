package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE deletedAt IS NULL ORDER BY id DESC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :sceneId AND deletedAt IS NULL LIMIT 1")
    fun observeById(sceneId: Long): Flow<SceneEntity?>

    @Query("SELECT * FROM scenes WHERE id = :sceneId AND deletedAt IS NULL LIMIT 1")
    suspend fun getById(sceneId: Long): SceneEntity?

    @Query("SELECT * FROM scenes WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC, id DESC")
    fun observeDeleted(): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(scene: SceneEntity): Long

    @Query("UPDATE scenes SET deletedAt = :deletedAt WHERE id = :sceneId")
    suspend fun softDeleteById(sceneId: Long, deletedAt: Long)

    @Query("UPDATE scenes SET deletedAt = NULL WHERE id = :sceneId")
    suspend fun restoreById(sceneId: Long)

    @Query("DELETE FROM scenes WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM scenes WHERE deletedAt IS NOT NULL AND deletedAt < :cutoffTimeMillis")
    suspend fun purgeDeletedBefore(cutoffTimeMillis: Long)

    @Query("DELETE FROM scenes WHERE id = :sceneId")
    suspend fun deleteById(sceneId: Long)
}
