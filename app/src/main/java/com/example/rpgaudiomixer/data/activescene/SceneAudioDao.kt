package com.example.rpgaudiomixer.data.activescene

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneAudioDao {
    @Query("""
        SELECT ssc.*, sc.name as categoryName 
        FROM scene_soundscape_cross_ref ssc
        INNER JOIN soundscape_categories sc ON ssc.categoryId = sc.id
        WHERE ssc.sceneId = :sceneId
        ORDER BY ssc.displayOrder ASC
    """)
    fun observeSoundscapesForScene(sceneId: Long): Flow<List<SceneSoundscapeCrossRef>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSoundscapeToScene(ref: SceneSoundscapeCrossRef)

    @Query("DELETE FROM scene_soundscape_cross_ref WHERE sceneId = :sceneId AND categoryId = :categoryId")
    suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long)

    @Update
    suspend fun updateSoundscapeRef(ref: SceneSoundscapeCrossRef)

    @Query("""
        SELECT sfc.*
        FROM scene_fx_cross_ref sfc
        WHERE sfc.sceneId = :sceneId
        ORDER BY sfc.displayOrder ASC
    """)
    fun observeFxForScene(sceneId: Long): Flow<List<SceneFxCrossRef>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFxToScene(ref: SceneFxCrossRef)

    @Query("DELETE FROM scene_fx_cross_ref WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId")
    suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long)

    @Update
    suspend fun updateFxRef(ref: SceneFxCrossRef)
}
