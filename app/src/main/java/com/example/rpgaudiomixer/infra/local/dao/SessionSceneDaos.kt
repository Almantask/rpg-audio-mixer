package com.example.rpgaudiomixer.infra.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.rpgaudiomixer.infra.local.entities.FXTrackEntity
import com.example.rpgaudiomixer.infra.local.entities.SceneFxCrossRef
import com.example.rpgaudiomixer.infra.local.entities.SceneEntity
import com.example.rpgaudiomixer.infra.local.entities.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.infra.local.entities.SessionEntity
import com.example.rpgaudiomixer.infra.local.entities.SessionSceneCrossRef
import com.example.rpgaudiomixer.infra.local.entities.SoundscapeCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE campaignId = :campaignId AND deletedAt IS NULL ORDER BY date DESC")
    fun observeByCampaign(campaignId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE campaignId = :campaignId AND deletedAt IS NULL ORDER BY date DESC LIMIT 1")
    fun observeLatestByCampaign(campaignId: Long): Flow<SessionEntity?>

    @Query("SELECT * FROM sessions WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SessionEntity>>

    @Query("UPDATE sessions SET lastOpenedSceneId = :sceneId WHERE id = :sessionId")
    suspend fun updateLastOpenedScene(sessionId: Long, sceneId: Long)

    @Upsert
    suspend fun upsert(session: SessionEntity)

    @Query("UPDATE sessions SET deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long)

    @Query("UPDATE sessions SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun permanentDelete(id: Long)

    @Query("DELETE FROM sessions WHERE deletedAt < :threshold")
    suspend fun purgeOldDeleted(threshold: Long)
}

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE deletedAt IS NULL ORDER BY name ASC")
    fun observeAll(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id")
    fun getById(id: Long): Flow<SceneEntity?>

    @Query("SELECT * FROM scenes WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<SceneEntity>>

    @Upsert
    suspend fun upsert(scene: SceneEntity)

    @Query("UPDATE scenes SET deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long)

    @Query("UPDATE scenes SET deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun permanentDelete(id: Long)

    @Query("DELETE FROM scenes WHERE deletedAt < :threshold")
    suspend fun purgeOldDeleted(threshold: Long)
}

@Dao
interface SessionSceneDao {
    @Transaction
    @Query("""
        SELECT scenes.* FROM scenes
        JOIN session_scene_cross_ref ON scenes.id = session_scene_cross_ref.sceneId
        WHERE session_scene_cross_ref.sessionId = :sessionId
    """)
    fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun link(crossRef: SessionSceneCrossRef)

    @Query("DELETE FROM session_scene_cross_ref WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun unlink(sessionId: Long, sceneId: Long)
}

@Dao
interface SceneSoundscapeDao {
    @Query("""
        SELECT soundscape_categories.* FROM soundscape_categories
        JOIN scene_soundscape_cross_ref ON soundscape_categories.id = scene_soundscape_cross_ref.categoryId
        WHERE scene_soundscape_cross_ref.sceneId = :sceneId
        ORDER BY scene_soundscape_cross_ref.displayOrder ASC
    """)
    fun observeCategoriesByScene(sceneId: Long): Flow<List<SoundscapeCategoryEntity>>

    @Query("SELECT * FROM scene_soundscape_cross_ref WHERE sceneId = :sceneId ORDER BY displayOrder ASC")
    fun observeCrossRefsByScene(sceneId: Long): Flow<List<SceneSoundscapeCrossRef>>

    @Upsert
    suspend fun upsert(crossRef: SceneSoundscapeCrossRef)

    @Query("DELETE FROM scene_soundscape_cross_ref WHERE sceneId = :sceneId AND categoryId = :categoryId")
    suspend fun delete(sceneId: Long, categoryId: Long)

    @Query("UPDATE scene_soundscape_cross_ref SET displayOrder = :order WHERE sceneId = :sceneId AND categoryId = :categoryId")
    suspend fun updateOrder(sceneId: Long, categoryId: Long, order: Int)

    @Query("UPDATE scene_soundscape_cross_ref SET mixVolume = :volume WHERE sceneId = :sceneId AND categoryId = :categoryId")
    suspend fun updateMixVolume(sceneId: Long, categoryId: Long, volume: Float)

    @Query("UPDATE scene_soundscape_cross_ref SET intensityLevel = :level WHERE sceneId = :sceneId AND categoryId = :categoryId")
    suspend fun updateIntensity(sceneId: Long, categoryId: Long, level: Int)
}

@Dao
interface SceneFxDao {
    @Query("""
        SELECT fx_tracks.* FROM fx_tracks
        JOIN scene_fx_cross_ref ON fx_tracks.id = scene_fx_cross_ref.fxTrackId
        WHERE scene_fx_cross_ref.sceneId = :sceneId
        ORDER BY scene_fx_cross_ref.displayOrder ASC
    """)
    fun observeFxByScene(sceneId: Long): Flow<List<FXTrackEntity>>

    @Upsert
    suspend fun upsert(crossRef: SceneFxCrossRef)

    @Query("DELETE FROM scene_fx_cross_ref WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId")
    suspend fun delete(sceneId: Long, fxTrackId: Long)

    @Query("UPDATE scene_fx_cross_ref SET displayOrder = :order WHERE sceneId = :sceneId AND fxTrackId = :fxTrackId")
    suspend fun updateOrder(sceneId: Long, fxTrackId: Long, order: Int)
}
