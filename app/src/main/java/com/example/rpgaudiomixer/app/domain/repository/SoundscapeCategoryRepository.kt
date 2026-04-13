package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory
import kotlinx.coroutines.flow.Flow

interface SoundscapeCategoryRepository {
    fun observeAll(): Flow<List<SoundscapeCategory>>
    fun observeDeleted(): Flow<List<SoundscapeCategory>>
    suspend fun createCategory(name: String, sortOrder: Int = 0): Long
    suspend fun updateCategory(category: SoundscapeCategory)
    suspend fun deleteCategory(id: Long)
    suspend fun restoreCategory(id: Long)
    suspend fun permanentlyDeleteCategory(id: Long)
}
