package com.example.rpgaudiomixer.infra.scene

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes ORDER BY name ASC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(scene: SceneEntity): Long

    @Query("DELETE FROM scenes WHERE id = :id")
    fun delete(id: Long): Int

    @Transaction
    @Query("SELECT * FROM scenes WHERE id = :sceneId")
    fun observeSceneWithSoundscapes(sceneId: Long): Flow<SceneWithSoundscapes?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertCrossRef(crossRef: SceneSoundscapeCrossRef): Long

    @Query("DELETE FROM scene_soundscape_cross_ref WHERE sceneId = :sceneId AND categoryId = :categoryId")
    fun deleteCrossRef(sceneId: Long, categoryId: Long): Int
    
    @Query("SELECT * FROM scene_soundscape_cross_ref WHERE sceneId = :sceneId ORDER BY displayOrder ASC")
    fun observeCrossRefsForScene(sceneId: Long): Flow<List<SceneSoundscapeCrossRef>>

    @Query("UPDATE scene_soundscape_cross_ref SET mixVolume = :mixVolume, intensityLevel = :intensityLevel WHERE sceneId = :sceneId AND categoryId = :categoryId")
    fun updateMetadata(sceneId: Long, categoryId: Long, mixVolume: Float, intensityLevel: Int)

    @Transaction
    @Query("SELECT * FROM scenes WHERE id = :sceneId")
    fun observeSceneWithFx(sceneId: Long): Flow<SceneWithFx?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertFxCrossRef(crossRef: SceneFxCrossRef): Long

    @Query("DELETE FROM scene_fx_cross_ref WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId")
    fun deleteFxCrossRef(sceneId: Long, fxTrackId: Long): Int
    
    @Query("SELECT * FROM scene_fx_cross_ref WHERE sceneId = :sceneId ORDER BY displayOrder ASC")
    fun observeFxCrossRefsForScene(sceneId: Long): Flow<List<SceneFxCrossRef>>

    @Query("UPDATE scene_soundscape_cross_ref SET displayOrder = :displayOrder WHERE sceneId = :sceneId AND categoryId = :categoryId")
    fun updateSoundscapeOrder(sceneId: Long, categoryId: Long, displayOrder: Int)

    @Query("UPDATE scene_fx_cross_ref SET displayOrder = :displayOrder WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId")
    fun updateFxOrder(sceneId: Long, fxTrackId: Long, displayOrder: Int)
}
