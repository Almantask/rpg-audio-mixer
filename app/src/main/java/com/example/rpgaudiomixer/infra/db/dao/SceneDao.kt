package com.example.rpgaudiomixer.infra.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.rpgaudiomixer.infra.db.entities.SceneEntity
import com.example.rpgaudiomixer.infra.db.entities.SceneFXTrackEntity
import com.example.rpgaudiomixer.infra.db.entities.SceneSoundscapeCategoryEntity
import com.example.rpgaudiomixer.infra.db.entities.SessionSceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {

    // ── Scenes ────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM scenes ORDER BY name ASC")
    fun getAllScenes(): Flow<List<SceneEntity>>

    @Query("""
        SELECT s.* FROM scenes s
        INNER JOIN session_scenes ss ON ss.sceneId = s.id
        WHERE ss.sessionId = :sessionId
        ORDER BY s.name ASC
    """)
    fun getScenesForSession(sessionId: Long): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id LIMIT 1")
    suspend fun getSceneById(id: Long): SceneEntity?

    @Upsert
    suspend fun upsertScene(scene: SceneEntity): Long

    @Query("DELETE FROM scenes WHERE id = :id")
    suspend fun deleteScene(id: Long)

    @Query("UPDATE scenes SET masterAtmosphereVolume = :volume WHERE id = :sceneId")
    suspend fun updateMasterAtmosphereVolume(sceneId: Long, volume: Float)

    @Query("UPDATE scenes SET masterSoundboardVolume = :volume WHERE id = :sceneId")
    suspend fun updateMasterSoundboardVolume(sceneId: Long, volume: Float)

    // ── Session ↔ Scene ────────────────────────────────────────────────────────

    @Upsert
    suspend fun addSceneToSession(link: SessionSceneEntity)

    @Query("DELETE FROM session_scenes WHERE sessionId = :sessionId AND sceneId = :sceneId")
    suspend fun removeSceneFromSession(sessionId: Long, sceneId: Long)

    // ── Scene Soundscape Categories ────────────────────────────────────────────

    @Query("""
        SELECT ssc.* FROM scene_soundscape_categories ssc
        WHERE ssc.sceneId = :sceneId
        ORDER BY ssc.sortOrder ASC
    """)
    fun getSceneSoundscapeCategoryEntities(sceneId: Long): Flow<List<SceneSoundscapeCategoryEntity>>

    @Upsert
    suspend fun upsertSceneSoundscapeCategory(entity: SceneSoundscapeCategoryEntity): Long

    @Query("UPDATE scene_soundscape_categories SET mixVolume = :volume WHERE id = :id")
    suspend fun updateCategoryMixVolume(id: Long, volume: Float)

    @Query("UPDATE scene_soundscape_categories SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateCategorySortOrder(id: Long, sortOrder: Int)

    @Query("DELETE FROM scene_soundscape_categories WHERE id = :id")
    suspend fun removeSceneSoundscapeCategory(id: Long)

    // ── Scene FX Tracks ────────────────────────────────────────────────────────

    @Query("""
        SELECT sft.* FROM scene_fx_tracks sft
        WHERE sft.sceneId = :sceneId
        ORDER BY sft.sortOrder ASC
    """)
    fun getSceneFXTrackEntities(sceneId: Long): Flow<List<SceneFXTrackEntity>>

    @Upsert
    suspend fun upsertSceneFXTrack(entity: SceneFXTrackEntity): Long

    @Query("UPDATE scene_fx_tracks SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateFXSortOrder(id: Long, sortOrder: Int)

    @Query("DELETE FROM scene_fx_tracks WHERE id = :id")
    suspend fun removeSceneFXTrack(id: Long)
}
