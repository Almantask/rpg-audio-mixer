package com.example.rpgaudiomixer.data.fx.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NULL ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query(
        """
        SELECT * FROM fx_tracks
        WHERE deletedAt IS NULL
        ORDER BY playCount DESC, name COLLATE NOCASE ASC, id ASC
        LIMIT 1
        """,
    )
    fun observeMostPlayedTrack(): Flow<FxTrackEntity?>

    @Query("SELECT * FROM fx_tracks WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC, id DESC")
    fun observeDeleted(): Flow<List<FxTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FxTrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<FxTrackEntity>)

    @Query("UPDATE fx_tracks SET playCount = playCount + 1 WHERE id = :trackId")
    suspend fun incrementPlayCount(trackId: Long)

    @Query("SELECT COUNT(*) FROM fx_tracks WHERE isDemoContent = 1 AND deletedAt IS NULL")
    suspend fun demoTrackCount(): Int

    @Query("UPDATE fx_tracks SET deletedAt = :deletedAt WHERE id = :trackId")
    suspend fun softDeleteById(trackId: Long, deletedAt: Long)

    @Query("UPDATE fx_tracks SET deletedAt = NULL WHERE id = :trackId")
    suspend fun restore(trackId: Long)

    @Query("DELETE FROM fx_tracks WHERE id = :trackId")
    suspend fun hardDeleteById(trackId: Long)

    @Query("DELETE FROM fx_tracks WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllDeleted()

    @Query("DELETE FROM fx_tracks WHERE deletedAt IS NOT NULL AND deletedAt <= :cutoffMillis")
    suspend fun purgeDeletedBefore(cutoffMillis: Long)
}
