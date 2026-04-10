package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneFxDao {
    @Query("SELECT * FROM scene_fx_cross_ref WHERE sceneId = :sceneId ORDER BY displayOrder ASC")
    fun observeByScene(sceneId: Long): Flow<List<SceneFxCrossRef>>

    @Upsert
    suspend fun upsert(crossRef: SceneFxCrossRef)

    @Query("DELETE FROM scene_fx_cross_ref WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId")
    suspend fun delete(sceneId: Long, fxTrackId: Long)

    @Query("""
        UPDATE scene_fx_cross_ref
        SET displayOrder = :displayOrder
        WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId
    """)
    suspend fun updateDisplayOrder(sceneId: Long, fxTrackId: Long, displayOrder: Int)

    @Transaction
    suspend fun updateDisplayOrders(sceneId: Long, fxTrackIds: List<Long>) {
        fxTrackIds.forEachIndexed { index, fxTrackId ->
            updateDisplayOrder(sceneId, fxTrackId, index)
        }
    }
}
