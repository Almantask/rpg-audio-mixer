package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE deletedAt IS NULL ORDER BY name ASC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id")
    suspend fun getById(id: Long): SceneEntity?

    @Upsert
    suspend fun upsert(scene: SceneEntity): Long

    @Query("UPDATE scenes SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE scenes SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Delete
    suspend fun delete(scene: SceneEntity)
}
