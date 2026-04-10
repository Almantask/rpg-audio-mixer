package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneFxDao {
    @Query(
        """
        SELECT
            refs.sceneId,
            refs.fxTrackId,
            refs.displayOrder,
            fx_tracks.name,
            fx_tracks.filePath,
            fx_tracks.tags,
            fx_tracks.durationMs,
            fx_tracks.playCount
        FROM scene_fx_cross_refs AS refs
        INNER JOIN fx_tracks
            ON fx_tracks.id = refs.fxTrackId
        WHERE refs.sceneId = :sceneId
          AND fx_tracks.deletedAt IS NULL
        ORDER BY refs.displayOrder ASC, refs.fxTrackId ASC
        """,
    )
    fun observeByScene(sceneId: Long): Flow<List<SceneFxListItemEntity>>

    @Query("SELECT fxTrackId FROM scene_fx_cross_refs WHERE sceneId = :sceneId ORDER BY displayOrder ASC")
    suspend fun getLinkedFxIds(sceneId: Long): List<Long>

    @Query("SELECT COALESCE(MAX(displayOrder), -1) FROM scene_fx_cross_refs WHERE sceneId = :sceneId")
    suspend fun getMaxDisplayOrder(sceneId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(crossRef: SceneFxCrossRef)

    @Query(
        """
        UPDATE scene_fx_cross_refs
        SET displayOrder = :displayOrder
        WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId
        """,
    )
    suspend fun updateDisplayOrder(sceneId: Long, fxTrackId: Long, displayOrder: Int)

    @Query("DELETE FROM scene_fx_cross_refs WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId")
    suspend fun delete(sceneId: Long, fxTrackId: Long)
}
