package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NULL ORDER BY name ASC, id ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query(
        """
        SELECT * FROM fx_tracks
        WHERE deletedAt IS NULL
          AND name LIKE '%' || :query || '%'
        ORDER BY name ASC, id ASC
        """
    )
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM fx_tracks WHERE isDemo = 1 AND deletedAt IS NULL LIMIT 1)")
    fun observeDemoContentAvailable(): Flow<Boolean>

    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC, id DESC")
    fun observeDeleted(): Flow<List<FxTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: FxTrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tracks: List<FxTrackEntity>)

    @Query("DELETE FROM fx_tracks WHERE id = :trackId")
    suspend fun deleteById(trackId: Long)

    @Query("UPDATE fx_tracks SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :trackId")
    suspend fun softDelete(trackId: Long, deletedAt: Long)

    @Query("UPDATE fx_tracks SET isDeleted = 0, deletedAt = NULL WHERE id = :trackId")
    suspend fun restore(trackId: Long)

    @Query("DELETE FROM fx_tracks WHERE deletedAt IS NOT NULL AND deletedAt < :cutoffTimeMillis")
    suspend fun purgeDeletedBefore(cutoffTimeMillis: Long)

    @Query("DELETE FROM fx_tracks WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("UPDATE fx_tracks SET playCount = playCount + 1 WHERE id = :trackId")
    suspend fun incrementPlayCount(trackId: Long)
}
