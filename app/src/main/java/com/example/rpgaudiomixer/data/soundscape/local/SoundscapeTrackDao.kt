package com.example.rpgaudiomixer.data.soundscape.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoundscapeTrackDao {
    @Query("SELECT * FROM soundscape_tracks WHERE categoryId = :categoryId ORDER BY id ASC")
    suspend fun getByCategory(categoryId: Long): List<SoundscapeTrackEntity>

    @Query(
        """
        SELECT
            soundscape_tracks.name AS trackName,
            soundscape_categories.name AS categoryName,
            soundscape_tracks.playCount AS playCount
        FROM soundscape_tracks
        INNER JOIN soundscape_categories
            ON soundscape_tracks.categoryId = soundscape_categories.id
        WHERE soundscape_categories.deletedAt IS NULL
        ORDER BY soundscape_tracks.playCount DESC, soundscape_tracks.name ASC
        LIMIT 1
        """,
    )
    fun observeMostPlayed(): Flow<TopSoundscapeTrackEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SoundscapeTrackEntity): Long

    @Query("DELETE FROM soundscape_tracks WHERE id IN (:trackIds)")
    suspend fun deleteByIds(trackIds: List<Long>)

    @Query("DELETE FROM soundscape_tracks WHERE categoryId = :categoryId")
    suspend fun deleteByCategory(categoryId: Long)
}
