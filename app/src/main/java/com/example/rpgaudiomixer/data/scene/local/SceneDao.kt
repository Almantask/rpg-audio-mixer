package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE deletedAt IS NULL ORDER BY id DESC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :sceneId AND deletedAt IS NULL")
    fun observeScene(sceneId: Long): Flow<SceneEntity?>

    @Query("SELECT * FROM scenes WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC, id DESC")
    fun observeDeleted(): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SceneEntity): Long

    @Query("UPDATE scenes SET masterVolume = :masterVolume WHERE id = :sceneId")
    suspend fun updateMasterVolume(sceneId: Long, masterVolume: Float)

    @Query("UPDATE scenes SET deletedAt = :deletedAt WHERE id = :sceneId")
    suspend fun softDeleteById(sceneId: Long, deletedAt: Long)

    @Query("UPDATE scenes SET deletedAt = NULL WHERE id = :sceneId")
    suspend fun restore(sceneId: Long)

    @Query("DELETE FROM scenes WHERE id = :sceneId")
    suspend fun hardDeleteById(sceneId: Long)

    @Query("DELETE FROM scenes WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM scenes WHERE deletedAt IS NOT NULL AND deletedAt <= :cutoffMillis")
    suspend fun purgeDeletedBefore(cutoffMillis: Long)
}
