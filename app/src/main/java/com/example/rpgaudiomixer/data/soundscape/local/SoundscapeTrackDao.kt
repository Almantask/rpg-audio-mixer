package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeTrackDao {
    @Query(
        """
        SELECT * FROM soundscape_tracks
        WHERE categoryId = :categoryId
        ORDER BY displayOrder ASC, id ASC
        """,
    )
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SoundscapeTrackEntity>)

    @Query("DELETE FROM soundscape_tracks WHERE categoryId = :categoryId")
    suspend fun deleteByCategoryId(categoryId: Long)
}
