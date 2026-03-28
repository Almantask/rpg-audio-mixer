package com.example.rpgaudiomixer.infra.storage.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rpgaudiomixer.infra.storage.db.entity.SceneEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SceneFxRefEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SceneSoundscapeRefEntity
import com.example.rpgaudiomixer.infra.storage.db.entity.SessionSceneCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes ORDER BY created_at DESC")
    fun getAllScenes(): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE id = :id")
    fun getSceneById(id: Long): Flow<SceneEntity?>

    @Query(
        """
        SELECT s.* FROM scenes s
        INNER JOIN session_scenes ss ON s.id = ss.scene_id
        WHERE ss.session_id = :sessionId
        ORDER BY ss.order_index ASC
        """
    )
    fun getScenesBySession(sessionId: Long): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scene: SceneEntity): Long

    @Update
    suspend fun update(scene: SceneEntity)

    @Delete
    suspend fun delete(scene: SceneEntity)

    // --- Session↔Scene cross-ref ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessionScene(ref: SessionSceneCrossRef)

    @Query("DELETE FROM session_scenes WHERE session_id = :sessionId AND scene_id = :sceneId")
    suspend fun deleteSessionScene(sessionId: Long, sceneId: Long)

    // --- Scene↔Soundscape refs ---

    @Query(
        """
        SELECT ssr.*, sc.id as cat_id, sc.name as cat_name, sc.parent_category, sc.created_at as cat_created_at
        FROM scene_soundscapes ssr
        INNER JOIN soundscape_categories sc ON sc.id = ssr.category_id
        WHERE ssr.scene_id = :sceneId
        ORDER BY ssr.order_index ASC
        """
    )
    fun getSceneSoundscapeRefs(sceneId: Long): Flow<List<SceneSoundscapeWithCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSceneSoundscape(ref: SceneSoundscapeRefEntity)

    @Query("DELETE FROM scene_soundscapes WHERE scene_id = :sceneId AND category_id = :categoryId")
    suspend fun deleteSceneSoundscape(sceneId: Long, categoryId: Long)

    @Query("UPDATE scene_soundscapes SET mix_volume = :mix WHERE scene_id = :sceneId AND category_id = :categoryId")
    suspend fun updateSceneSoundscapeMix(sceneId: Long, categoryId: Long, mix: Float)

    @Query("UPDATE scene_soundscapes SET active_intensity = :intensity WHERE scene_id = :sceneId AND category_id = :categoryId")
    suspend fun updateSceneSoundscapeIntensity(sceneId: Long, categoryId: Long, intensity: Int)

    @Query("UPDATE scene_soundscapes SET order_index = :orderIndex WHERE scene_id = :sceneId AND category_id = :categoryId")
    suspend fun updateSceneSoundscapeOrder(sceneId: Long, categoryId: Long, orderIndex: Int)

    // --- Scene↔FX refs ---

    @Query(
        """
        SELECT sfr.*, fe.id as fx_id, fe.name as fx_name, fe.track_file_path, fe.tags, fe.duration_ms, fe.play_count, fe.created_at as fx_created_at
        FROM scene_fx sfr
        INNER JOIN fx_effects fe ON fe.id = sfr.fx_effect_id
        WHERE sfr.scene_id = :sceneId
        ORDER BY sfr.order_index ASC
        """
    )
    fun getSceneFxRefs(sceneId: Long): Flow<List<SceneFxWithEffect>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSceneFx(ref: SceneFxRefEntity)

    @Query("DELETE FROM scene_fx WHERE scene_id = :sceneId AND fx_effect_id = :fxEffectId")
    suspend fun deleteSceneFx(sceneId: Long, fxEffectId: Long)

    @Query("UPDATE scene_fx SET order_index = :orderIndex WHERE scene_id = :sceneId AND fx_effect_id = :fxEffectId")
    suspend fun updateSceneFxOrder(sceneId: Long, fxEffectId: Long, orderIndex: Int)
}
