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
        SELECT sfx.sceneId,
               sfx.fxTrackId,
               fx.name,
               fx.filePath,
               fx.tags,
               fx.durationMs,
               fx.playCount,
               fx.isDemoContent,
               sfx.displayOrder
        FROM scene_fx_cross_refs sfx
        INNER JOIN fx_tracks fx ON fx.id = sfx.fxTrackId
        WHERE sfx.sceneId = :sceneId
        ORDER BY sfx.displayOrder ASC, fx.name COLLATE NOCASE ASC
        """,
    )
    fun observeFxByScene(sceneId: Long): Flow<List<SceneFxRow>>

    @Query(
        """
        SELECT fxTrackId
        FROM scene_fx_cross_refs
        WHERE sceneId = :sceneId
        ORDER BY displayOrder ASC
        """,
    )
    fun observeLinkedFxTrackIds(sceneId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: SceneFxCrossRef)

    @Query(
        """
        SELECT COALESCE(MAX(displayOrder) + 1, 0)
        FROM scene_fx_cross_refs
        WHERE sceneId = :sceneId
        """,
    )
    suspend fun nextDisplayOrder(sceneId: Long): Int

    @Query(
        """
        DELETE FROM scene_fx_cross_refs
        WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId
        """,
    )
    suspend fun delete(sceneId: Long, fxTrackId: Long)

    @Query(
        """
        UPDATE scene_fx_cross_refs
        SET displayOrder = :displayOrder
        WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId
        """,
    )
    suspend fun updateDisplayOrder(sceneId: Long, fxTrackId: Long, displayOrder: Int)
}
