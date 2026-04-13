package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.app.data.local.entities.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE isDeleted = 1")
    fun observeDeleted(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id")
    suspend fun getById(id: Long): SceneEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(scene: SceneEntity): Long

    @Query("UPDATE scenes SET isDeleted = 1, deletedAt = (strftime('%s','now') * 1000) WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("UPDATE scenes SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun permanentlyDelete(id: Long)

    @Query("DELETE FROM scenes")
    suspend fun deleteAll()
}
