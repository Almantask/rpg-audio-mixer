package com.example.rpgaudiomixer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {
    @Query("SELECT * FROM soundscape_categories ORDER BY name ASC")
    fun observeAll(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :id")
    suspend fun getById(id: Long): SoundscapeCategoryEntity?

    @Upsert
    suspend fun upsert(category: SoundscapeCategoryEntity): Long

    @Delete
    suspend fun delete(category: SoundscapeCategoryEntity)
}
