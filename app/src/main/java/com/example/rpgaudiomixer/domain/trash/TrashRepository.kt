package com.example.rpgaudiomixer.domain.trash

import com.example.rpgaudiomixer.domain.model.TrashItem
import com.example.rpgaudiomixer.domain.model.TrashItemType
import kotlinx.coroutines.flow.Flow

interface TrashRepository {
    fun observeDeletedItems(): Flow<List<TrashItem>>

    suspend fun restoreItem(itemId: Long, itemType: TrashItemType)

    suspend fun permanentlyDeleteItem(itemId: Long, itemType: TrashItemType)

    suspend fun emptyVault()

    suspend fun purgeExpiredItems(currentTimeMillis: Long = System.currentTimeMillis())
}
