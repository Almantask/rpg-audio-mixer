package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class MostPlayedSoundscapeTrackRow(
    val trackId: Long,
    val trackName: String,
    val categoryName: String,
    val playCount: Int,
)

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY id ASC")
    fun observeByCategory(categoryId: Long): Flow<List<SoundscapeTrackEntity>>

    @Query("SELECT * FROM soundscape_tracks WHERE id = :trackId LIMIT 1")
    suspend fun getById(trackId: Long): SoundscapeTrackEntity?

    @Query(
        """
        SELECT soundscape_tracks.id AS trackId,
               soundscape_tracks.name AS trackName,
               soundscape_categories.name AS categoryName,
               soundscape_tracks.playCount AS playCount
        FROM soundscape_tracks
        INNER JOIN soundscape_categories ON soundscape_categories.id = soundscape_tracks.categoryId
        WHERE soundscape_tracks.playCount > 0
        ORDER BY soundscape_tracks.playCount DESC,
                 soundscape_tracks.name COLLATE NOCASE ASC,
                 soundscape_tracks.id ASC
        LIMIT 1
        """,
    )
    fun observeMostPlayed(): Flow<MostPlayedSoundscapeTrackRow?>

    @Query("SELECT EXISTS(SELECT 1 FROM soundscape_tracks WHERE filePath LIKE 'demo://%')")
    fun hasDemoTracks(): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: SoundscapeTrackEntity): Long

    @Query("DELETE FROM soundscape_tracks WHERE id = :trackId")
    suspend fun deleteById(trackId: Long)

    @Query("DELETE FROM soundscape_tracks WHERE categoryId = :categoryId AND id NOT IN (:trackIds)")
    suspend fun deleteMissingFromCategory(categoryId: Long, trackIds: List<Long>)

    @Query("DELETE FROM soundscape_tracks WHERE categoryId = :categoryId")
    suspend fun deleteByCategory(categoryId: Long)

    @Query("DELETE FROM soundscape_tracks")
    suspend fun clearAll()
}
