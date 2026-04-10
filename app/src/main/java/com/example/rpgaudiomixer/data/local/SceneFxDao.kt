package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneFxDao {
    @Query(
        """
        SELECT scene_fx_cross_ref.sceneId AS sceneId,
               fx_tracks.id AS fxTrackId,
               fx_tracks.name AS name,
               fx_tracks.filePath AS filePath,
               fx_tracks.tags AS tags,
               fx_tracks.durationMs AS durationMs,
               fx_tracks.playCount AS playCount,
               fx_tracks.isDemo AS isDemo,
               scene_fx_cross_ref.displayOrder AS displayOrder
        FROM scene_fx_cross_ref
        INNER JOIN fx_tracks
            ON scene_fx_cross_ref.fxTrackId = fx_tracks.id
        WHERE scene_fx_cross_ref.sceneId = :sceneId
          AND fx_tracks.deletedAt IS NULL
        ORDER BY scene_fx_cross_ref.displayOrder ASC, fx_tracks.name ASC
        """
    )
    fun observeFxByScene(sceneId: Long): Flow<List<SceneFxSummaryEntity>>

    @Query(
        """
        SELECT COALESCE(MAX(displayOrder) + 1, 0)
        FROM scene_fx_cross_ref
        WHERE sceneId = :sceneId
        """
    )
    suspend fun getNextDisplayOrder(sceneId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(crossRef: SceneFxCrossRef)

    @Query("SELECT * FROM scene_fx_cross_ref WHERE sceneId = :sceneId ORDER BY displayOrder ASC, fxTrackId ASC")
    suspend fun getCrossRefs(sceneId: Long): List<SceneFxCrossRef>

    @Transaction
    suspend fun updateAll(crossRefs: List<SceneFxCrossRef>) {
        crossRefs.forEach { crossRef -> upsert(crossRef) }
    }

    @Query(
        """
        DELETE FROM scene_fx_cross_ref
        WHERE sceneId = :sceneId
          AND fxTrackId = :fxTrackId
        """
    )
    suspend fun remove(sceneId: Long, fxTrackId: Long)
}
