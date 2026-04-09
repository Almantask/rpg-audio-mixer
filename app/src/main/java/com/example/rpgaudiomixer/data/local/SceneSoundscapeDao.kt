package com.example.rpgaudiomixer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneSoundscapeDao {
    @Query("""
        SELECT * FROM scene_soundscape_cross_ref
        WHERE sceneId = :sceneId
        ORDER BY displayOrder ASC
    """)
    fun observeByScene(sceneId: Long): Flow<List<SceneSoundscapeCrossRef>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(crossRef: SceneSoundscapeCrossRef)

    @Query("DELETE FROM scene_soundscape_cross_ref WHERE sceneId = :sceneId AND categoryId = :categoryId")
    suspend fun delete(sceneId: Long, categoryId: Long)

    @Query("DELETE FROM scene_soundscape_cross_ref WHERE sceneId = :sceneId")
    suspend fun deleteAllForScene(sceneId: Long)

    @Transaction
    suspend fun updateDisplayOrders(sceneId: Long, categoryIds: List<Long>) {
        categoryIds.forEachIndexed { index, categoryId ->
            updateDisplayOrder(sceneId, categoryId, index)
        }
    }

    @Query("""
        UPDATE scene_soundscape_cross_ref
        SET displayOrder = :displayOrder
        WHERE sceneId = :sceneId AND categoryId = :categoryId
    """)
    suspend fun updateDisplayOrder(sceneId: Long, categoryId: Long, displayOrder: Int)

    @Query("""
        UPDATE scene_soundscape_cross_ref
        SET mixVolume = :mixVolume
        WHERE sceneId = :sceneId AND categoryId = :categoryId
    """)
    suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolume: Float)

    @Query("""
        UPDATE scene_soundscape_cross_ref
        SET intensityLevel = :intensityLevel
        WHERE sceneId = :sceneId AND categoryId = :categoryId
    """)
    suspend fun updateIntensityLevel(sceneId: Long, categoryId: Long, intensityLevel: Int)
}
