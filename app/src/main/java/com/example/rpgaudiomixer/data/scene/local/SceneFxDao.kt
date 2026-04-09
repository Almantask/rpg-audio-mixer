package com.example.rpgaudiomixer.data.scene.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class SceneFxRow(
    val sceneId: Long,
    val fxTrackId: Long,
    val name: String,
    val filePath: String,
    val tagsCsv: String,
    val durationMs: Long,
    val playCount: Int,
    val displayOrder: Int,
)

@Dao
interface SceneFxDao {
    @Query(
        """
        SELECT scene_fx_cross_refs.sceneId AS sceneId,
               scene_fx_cross_refs.fxTrackId AS fxTrackId,
               fx_tracks.name AS name,
               fx_tracks.filePath AS filePath,
               fx_tracks.tagsCsv AS tagsCsv,
               fx_tracks.durationMs AS durationMs,
               fx_tracks.playCount AS playCount,
               scene_fx_cross_refs.displayOrder AS displayOrder
        FROM scene_fx_cross_refs
        INNER JOIN fx_tracks ON fx_tracks.id = scene_fx_cross_refs.fxTrackId
        WHERE scene_fx_cross_refs.sceneId = :sceneId
        ORDER BY scene_fx_cross_refs.displayOrder ASC,
                 fx_tracks.name COLLATE NOCASE ASC,
                 scene_fx_cross_refs.fxTrackId ASC
        """,
    )
    fun observeByScene(sceneId: Long): Flow<List<SceneFxRow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(crossRef: SceneFxCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(crossRefs: List<SceneFxCrossRef>)

    @Query("DELETE FROM scene_fx_cross_refs WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId")
    suspend fun delete(sceneId: Long, fxTrackId: Long)

    @Query("DELETE FROM scene_fx_cross_refs WHERE sceneId = :sceneId")
    suspend fun deleteByScene(sceneId: Long)

    @Query("DELETE FROM scene_fx_cross_refs")
    suspend fun clearAll()

    @Query("SELECT MAX(displayOrder) FROM scene_fx_cross_refs WHERE sceneId = :sceneId")
    suspend fun maxDisplayOrder(sceneId: Long): Int?

    @Query("SELECT * FROM scene_fx_cross_refs WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId LIMIT 1")
    suspend fun get(sceneId: Long, fxTrackId: Long): SceneFxCrossRef?
}
