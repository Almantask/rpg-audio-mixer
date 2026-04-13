package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.app.data.local.entities.SoundscapeCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeCategoryDao {
    @Query("SELECT * FROM soundscape_categories WHERE isDeleted = 0 ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE isDeleted = 1")
    fun observeDeleted(): Flow<List<SoundscapeCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: SoundscapeCategoryEntity): Long

    @Query(
        "UPDATE soundscape_categories SET isDeleted = 1, " +
            "deletedAt = (strftime('%s','now') * 1000) WHERE id = :id"
    )
    suspend fun softDelete(id: Long)

    @Query("UPDATE soundscape_categories SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM soundscape_categories WHERE id = :id")
    suspend fun permanentlyDelete(id: Long)

    @Query("DELETE FROM soundscape_categories")
    suspend fun deleteAll()
}
