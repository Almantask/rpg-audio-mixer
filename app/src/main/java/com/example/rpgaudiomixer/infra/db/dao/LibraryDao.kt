package com.example.rpgaudiomixer.infra.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rpgaudiomixer.infra.db.entities.FXTrackEntity
import com.example.rpgaudiomixer.infra.db.entities.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.db.entities.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    // ── Soundscape Categories ──────────────────────────────────────────────────

    @Query("SELECT * FROM soundscape_categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM soundscape_categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: Long): SoundscapeCategoryEntity?

    @Upsert
    suspend fun upsertCategory(category: SoundscapeCategoryEntity): Long

    @Query("DELETE FROM soundscape_categories WHERE id = :id")
    suspend fun deleteCategory(id: Long)

    // ── Tracks ─────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM tracks WHERE categoryId = :categoryId ORDER BY intensityLevel ASC, name ASC")
    suspend fun getTracksForCategory(categoryId: Long): List<TrackEntity>

    @Upsert
    suspend fun upsertTrack(track: TrackEntity): Long

    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteTrack(id: Long)

    @Query("UPDATE tracks SET mixVolume = :volume WHERE id = :id")
    suspend fun updateTrackMixVolume(id: Long, volume: Float)

    @Query("UPDATE tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementTrackPlayCount(id: Long)

    @Query("SELECT * FROM tracks ORDER BY playCount DESC LIMIT 1")
    suspend fun getMostPlayedTrack(): TrackEntity?

    // ── FX Tracks ──────────────────────────────────────────────────────────────

    @Query("SELECT * FROM fx_tracks ORDER BY name ASC")
    fun getAllFXTracks(): Flow<List<FXTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE id = :id LIMIT 1")
    suspend fun getFXTrackById(id: Long): FXTrackEntity?

    @Upsert
    suspend fun upsertFXTrack(fxTrack: FXTrackEntity): Long

    @Query("DELETE FROM fx_tracks WHERE id = :id")
    suspend fun deleteFXTrack(id: Long)

    @Query("UPDATE fx_tracks SET playCount = playCount + 1 WHERE id = :id")
    suspend fun incrementFXPlayCount(id: Long)

    @Query("SELECT * FROM fx_tracks ORDER BY playCount DESC LIMIT 1")
    suspend fun getMostPlayedFXTrack(): FXTrackEntity?
}
