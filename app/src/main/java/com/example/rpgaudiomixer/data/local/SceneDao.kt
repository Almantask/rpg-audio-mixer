package com.example.rpgaudiomixer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes ORDER BY name ASC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id")
    suspend fun getById(id: Long): SceneEntity?

    @Upsert
    suspend fun upsert(scene: SceneEntity): Long

    @Delete
    suspend fun delete(scene: SceneEntity)

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
