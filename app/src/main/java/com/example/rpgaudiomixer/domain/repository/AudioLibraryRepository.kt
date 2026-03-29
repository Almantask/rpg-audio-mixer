package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.FX
import kotlinx.coroutines.flow.Flow

interface SoundscapeCategoryRepository {
    fun observeAll(): Flow<List<SoundscapeCategory>>
    suspend fun upsert(category: SoundscapeCategory): Long
    suspend fun delete(category: SoundscapeCategory)
}

interface FXRepository {
    fun observeAll(): Flow<List<FX>>
    suspend fun upsert(fx: FX): Long
    suspend fun delete(fx: FX)
}
