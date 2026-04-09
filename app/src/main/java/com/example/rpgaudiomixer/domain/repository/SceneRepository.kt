package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow

interface SceneRepository {
    fun observeAll(): Flow<List<Scene>>
    suspend fun getById(id: Long): Scene?
    suspend fun create(name: String, description: String? = null, tags: List<String> = emptyList()): Long
    suspend fun update(scene: Scene)
    suspend fun delete(scene: Scene)
}
