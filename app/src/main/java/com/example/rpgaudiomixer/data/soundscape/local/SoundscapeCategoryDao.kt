package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {

    @Query("SELECT * FROM soundscape_categories ORDER BY name ASC")
    fun observeAll(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :categoryId")
    fun observeById(categoryId: Long): Flow<SoundscapeCategoryEntity?>

    @Query("SELECT * FROM soundscape_categories WHERE id = :categoryId")
    suspend fun getById(categoryId: Long): SoundscapeCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: SoundscapeCategoryEntity): Long

    @Update
    suspend fun update(category: SoundscapeCategoryEntity)

    @Query("DELETE FROM soundscape_categories WHERE id = :categoryId")
    suspend fun delete(categoryId: Long)
}
