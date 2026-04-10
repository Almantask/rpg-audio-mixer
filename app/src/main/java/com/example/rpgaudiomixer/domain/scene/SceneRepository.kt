package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeAll(): Flow<List<Scene>>
    suspend fun createScene(
        name: String,
        description: String?,
        tags: List<String>,
    ): Long

    suspend fun deleteScene(sceneId: Long)
    suspend fun getScene(sceneId: Long): Scene?
}
