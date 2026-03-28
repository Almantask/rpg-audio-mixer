package com.example.rpgaudiomixer.domain.storage

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeLayer
import kotlinx.coroutines.flow.Flow

interface SoundscapeRepository {
    fun getAllCategories(): Flow<List<SoundscapeCategory>>
    fun getCategoryById(id: Long): Flow<SoundscapeCategory?>
    fun getLayersForCategory(categoryId: Long): Flow<List<SoundscapeLayer>>
    suspend fun insertCategory(category: SoundscapeCategory): Long
    suspend fun updateCategory(category: SoundscapeCategory)
    suspend fun deleteCategory(category: SoundscapeCategory)
    suspend fun insertLayer(layer: SoundscapeLayer): Long
    suspend fun updateLayer(layer: SoundscapeLayer)
    suspend fun deleteLayer(layer: SoundscapeLayer)
}
