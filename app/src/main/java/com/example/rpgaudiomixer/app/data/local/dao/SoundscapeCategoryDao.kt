package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.app.data.local.entities.SoundscapeCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {
    @Query("SELECT * FROM soundscape_categories WHERE sceneId = :sceneId ORDER BY position ASC")
    fun observeByScene(sceneId: Long): Flow<List<SoundscapeCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: SoundscapeCategoryEntity): Long

    @Query("DELETE FROM soundscape_categories WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM soundscape_categories")
    suspend fun deleteAll()
}
