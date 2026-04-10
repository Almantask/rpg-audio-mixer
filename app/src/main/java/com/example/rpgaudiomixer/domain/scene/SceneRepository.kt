package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeScenes(): Flow<List<Scene>>

    fun observeScene(sceneId: Long): Flow<Scene?>

    suspend fun createScene(name: String, description: String?, tags: List<String>): Long

    suspend fun updateMasterVolume(sceneId: Long, masterVolume: Float)

    suspend fun deleteScene(sceneId: Long, deletedAtMillis: Long = System.currentTimeMillis())
}
