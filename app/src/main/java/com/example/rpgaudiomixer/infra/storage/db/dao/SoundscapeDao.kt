package com.example.rpgaudiomixer.infra.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rpgaudiomixer.infra.storage.db.entity.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SoundscapeLayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeDao {
    @Query("SELECT * FROM soundscape_categories ORDER BY created_at ASC")
    fun getAllCategories(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :id")
    fun getCategoryById(id: Long): Flow<SoundscapeCategoryEntity?>

    @Query("SELECT * FROM soundscape_layers WHERE category_id = :categoryId ORDER BY intensity ASC")
    fun getLayersForCategory(categoryId: Long): Flow<List<SoundscapeLayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: SoundscapeCategoryEntity): Long

    @Update
    suspend fun updateCategory(category: SoundscapeCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: SoundscapeCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLayer(layer: SoundscapeLayerEntity): Long

    @Update
    suspend fun updateLayer(layer: SoundscapeLayerEntity)

    @Delete
    suspend fun deleteLayer(layer: SoundscapeLayerEntity)
}
