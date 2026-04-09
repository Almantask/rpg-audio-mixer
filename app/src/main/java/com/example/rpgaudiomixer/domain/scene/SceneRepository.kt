package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeScenes(): Flow<List<Scene>>
    fun observeScene(sceneId: Long): Flow<Scene?>
    suspend fun upsertScene(scene: Scene): Long
    suspend fun deleteScene(sceneId: Long)
    suspend fun addSoundscapeCategory(sceneId: Long, categoryName: String)
    suspend fun clearAll()
}
