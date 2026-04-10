package com.example.rpgaudiomixer.domain.trash

import com.example.rpgaudiomixer.domain.model.TrashItem
import com.example.rpgaudiomixer.domain.model.TrashItemType
import kotlinx.coroutines.flow.Flow

interface TrashRepository {
    fun observeDeletedItems(): Flow<List<TrashItem>>
    suspend fun restore(type: TrashItemType, id: Long)
    suspend fun permanentlyDelete(type: TrashItemType, id: Long)
    suspend fun emptyVault()
    suspend fun purgeExpired()
}
