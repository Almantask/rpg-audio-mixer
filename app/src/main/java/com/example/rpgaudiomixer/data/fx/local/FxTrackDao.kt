package com.example.rpgaudiomixer.data.fx.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FxTrackDao {
    @Query("SELECT * FROM fx_tracks ORDER BY name ASC")
    fun observeAll(): Flow<List<FxTrackEntity>>

    @Query("SELECT * FROM fx_tracks ORDER BY playCount DESC, name ASC LIMIT 1")
    fun observeMostPlayed(): Flow<FxTrackEntity?>

    @Query(
        """
        SELECT * FROM fx_tracks
        WHERE name LIKE '%' || :query || '%'
           OR tags LIKE '%' || :query || '%'
        ORDER BY name ASC
        """,
    )
    fun search(query: String): Flow<List<FxTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FxTrackEntity): Long

    @Query("DELETE FROM fx_tracks WHERE id = :trackId")
    suspend fun delete(trackId: Long)
}
