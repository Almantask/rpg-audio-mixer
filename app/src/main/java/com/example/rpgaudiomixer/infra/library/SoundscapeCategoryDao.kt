package com.example.rpgaudiomixer.infra.library

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {
    @Query("SELECT * FROM soundscape_categories ORDER BY name ASC")
    fun observeAll(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :id")
    fun getById(id: Long): SoundscapeCategoryEntity?

    @Query("SELECT * FROM soundscape_categories WHERE id = :id")
    fun observeById(id: Long): Flow<SoundscapeCategoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(category: SoundscapeCategoryEntity): Long

    @Query("DELETE FROM soundscape_categories WHERE id = :id")
    fun delete(id: Long): Int
}
