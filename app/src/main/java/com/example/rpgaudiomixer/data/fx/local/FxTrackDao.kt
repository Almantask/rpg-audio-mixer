package com.example.rpgaudiomixer.data.fx.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks WHERE playCount > 0 ORDER BY playCount DESC, name COLLATE NOCASE ASC, id ASC LIMIT 1")
    fun observeMostPlayed(): Flow<FxTrackEntity?>

    @Query(
        """
        SELECT * FROM fx_tracks
        WHERE name LIKE '%' || :query || '%'
           OR tagsCsv LIKE '%' || :query || '%'
        ORDER BY name COLLATE NOCASE ASC, id ASC
        """,
    )
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM fx_tracks WHERE filePath LIKE 'demo://%')")
    fun hasDemoTracks(): Flow<Boolean>

    @Query("SELECT * FROM fx_tracks WHERE id = :trackId LIMIT 1")
    suspend fun getById(trackId: Long): FxTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: FxTrackEntity): Long

    @Query("DELETE FROM fx_tracks WHERE id = :trackId")
    suspend fun deleteById(trackId: Long)

    @Query("DELETE FROM fx_tracks")
    suspend fun clearAll()
}
