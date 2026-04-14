package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory
import kotlinx.coroutines.flow.Flow

interface SoundscapeCategoryRepository {
    fun observeByScene(sceneId: Long): Flow<List<SoundscapeCategory>>
    suspend fun addCategory(sceneId: Long, name: String)
    suspend fun deleteCategory(id: Long)
    suspend fun deleteAll()
}
