package com.example.rpgaudiomixer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rpgaudiomixer.app.data.local.entities.AudioTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioTrackDao {
    @Query("SELECT * FROM audio_tracks WHERE isDeleted = 0")
    fun observeAll(): Flow<List<AudioTrackEntity>>

    @Query("SELECT * FROM audio_tracks WHERE type = :type AND isDeleted = 0")
    fun observeByType(type: String): Flow<List<AudioTrackEntity>>

    @Query("SELECT * FROM audio_tracks WHERE isDeleted = 1")
    fun observeDeleted(): Flow<List<AudioTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: AudioTrackEntity): Long

    @Query("UPDATE audio_tracks SET isDeleted = 1, deletedAt = (strftime('%s','now') * 1000) WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("UPDATE audio_tracks SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM audio_tracks WHERE id = :id")
    suspend fun permanentlyDelete(id: Long)

    @Query("DELETE FROM audio_tracks")
    suspend fun deleteAll()
}
