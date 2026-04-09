package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {
    @Query("SELECT * FROM soundscape_categories WHERE deletedAt IS NULL ORDER BY name ASC")
    fun observeAll(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :id")
    suspend fun getById(id: Long): SoundscapeCategoryEntity?

    @Upsert
    suspend fun upsert(category: SoundscapeCategoryEntity): Long

    @Query("UPDATE soundscape_categories SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: Long, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE soundscape_categories SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Delete
    suspend fun delete(category: SoundscapeCategoryEntity)
}
