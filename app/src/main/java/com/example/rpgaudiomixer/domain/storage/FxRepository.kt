package com.example.rpgaudiomixer.domain.storage

import com.example.rpgaudiomixer.domain.model.FxEffect
import kotlinx.coroutines.flow.Flow

interface FxRepository {
    fun getAllEffects(): Flow<List<FxEffect>>
    fun getEffectById(id: Long): Flow<FxEffect?>
    suspend fun insert(effect: FxEffect): Long
    suspend fun update(effect: FxEffect)
    suspend fun delete(effect: FxEffect)
    suspend fun incrementPlayCount(id: Long)
}
