package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :sceneId LIMIT 1")
    fun observeById(sceneId: Long): Flow<SceneEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(scene: SceneEntity): Long

    @Query("DELETE FROM scenes WHERE id = :sceneId")
    suspend fun deleteById(sceneId: Long)

    @Query("DELETE FROM scenes")
    suspend fun clearAll()
}
