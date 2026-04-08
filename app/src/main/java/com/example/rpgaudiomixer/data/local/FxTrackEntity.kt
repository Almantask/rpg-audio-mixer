package com.example.rpgaudiomixer.data.local

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Room entity for FX (sound effects) tracks.
 *
 * FX tracks are one-shot audio clips that can be triggered in soundboards.
 */
@Entity(tableName = "fx_tracks")
data class FxTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "file_path")
    val filePath: String,

    @ColumnInfo(name = "tags")
    val tags: String = "", // Comma-separated tags

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null // Null = not deleted, timestamp = soft-deleted
)

/**
 * DAO for FX track operations.
 */
@Dao
interface FxTrackDao {

    /**
     * Observe all non-deleted FX tracks.
     */
    @Query("SELECT * FROM fx_tracks WHERE deleted_at IS NULL ORDER BY created_at DESC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    /**
     * Search FX tracks by name or tags (non-deleted only).
     */
    @Query("""
        SELECT * FROM fx_tracks
        WHERE deleted_at IS NULL
        AND (name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')
        ORDER BY created_at DESC
    """)
    fun search(query: String): Flow<List<FxTrackEntity>>

    /**
     * Get FX track by ID.
     */
    @Query("SELECT * FROM fx_tracks WHERE id = :id")
    suspend fun getById(id: Long): FxTrackEntity?

    /**
     * Insert or update an FX track.
     */
    @Upsert
    suspend fun upsert(track: FxTrackEntity): Long

    /**
     * Soft-delete an FX track by setting deletedAt timestamp.
     */
    @Query("UPDATE fx_tracks SET deleted_at = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long = System.currentTimeMillis())

    /**
     * Permanently delete an FX track.
     */
    @Query("DELETE FROM fx_tracks WHERE id = :id")
    suspend fun hardDelete(id: Long)

    /**
     * Observe all soft-deleted FX tracks (for Trash/Vault of Echoes).
     */
    @Query("SELECT * FROM fx_tracks WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC")
    fun observeDeleted(): Flow<List<FxTrackEntity>>

    /**
     * Restore a soft-deleted FX track.
     */
    @Query("UPDATE fx_tracks SET deleted_at = NULL WHERE id = :id")
    suspend fun restore(id: Long)
}
