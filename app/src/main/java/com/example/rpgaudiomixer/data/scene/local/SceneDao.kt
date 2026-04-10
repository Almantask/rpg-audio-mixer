package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes ORDER BY id DESC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :sceneId")
    fun observeScene(sceneId: Long): Flow<SceneEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SceneEntity): Long

    @Query("UPDATE scenes SET masterVolume = :masterVolume WHERE id = :sceneId")
    suspend fun updateMasterVolume(sceneId: Long, masterVolume: Float)

    @Query("DELETE FROM scenes WHERE id = :sceneId")
    suspend fun deleteById(sceneId: Long)
}
