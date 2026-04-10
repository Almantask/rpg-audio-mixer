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

    @Query(
        """
        SELECT soundscape_tracks.id,
               soundscape_tracks.categoryId,
               soundscape_categories.name AS categoryName,
               soundscape_tracks.name,
               soundscape_tracks.filePath,
               soundscape_tracks.intensityLevel,
               soundscape_tracks.mixVolumePercent,
               soundscape_tracks.displayOrder,
               soundscape_tracks.playCount
        FROM soundscape_tracks
        INNER JOIN soundscape_categories ON soundscape_categories.id = soundscape_tracks.categoryId
        ORDER BY soundscape_tracks.playCount DESC,
                 soundscape_tracks.name COLLATE NOCASE ASC,
                 soundscape_tracks.id ASC
        LIMIT 1
        """,
    )
    fun observeMostPlayedTrack(): Flow<SoundscapeMostPlayedTrackRow?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SoundscapeTrackEntity>)

    @Query("DELETE FROM soundscape_tracks WHERE categoryId = :categoryId")
    suspend fun deleteByCategoryId(categoryId: Long)
}
