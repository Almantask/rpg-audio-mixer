package com.example.rpgaudiomixer.data.fx.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NULL ORDER BY name ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NULL ORDER BY playCount DESC, name ASC LIMIT 1")
    fun observeMostPlayed(): Flow<FxTrackEntity?>

    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<FxTrackEntity>>

    @Query(
        """
        SELECT * FROM fx_tracks
        WHERE deletedAt IS NULL
          AND (
              name LIKE '%' || :query || '%'
              OR tags LIKE '%' || :query || '%'
          )
        ORDER BY name ASC
        """,
    )
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FxTrackEntity): Long

    @Query("UPDATE fx_tracks SET deletedAt = :deletedAt WHERE id = :trackId")
    suspend fun softDelete(trackId: Long, deletedAt: Long)

    @Query("UPDATE fx_tracks SET deletedAt = NULL WHERE id = :trackId")
    suspend fun restore(trackId: Long)

    @Query("DELETE FROM fx_tracks WHERE id = :trackId")
    suspend fun delete(trackId: Long)

    @Query("DELETE FROM fx_tracks WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM fx_tracks WHERE deletedAt IS NOT NULL AND deletedAt < :cutoffMillis")
    suspend fun purgeDeletedBefore(cutoffMillis: Long)
}
