package com.example.rpgaudiomixer.domain.trash

import kotlinx.coroutines.flow.Flow

enum class DeletedItemType {
    CAMPAIGN,
    SESSION,
    SCENE,
    SOUNDSCAPE,
    FX,
}

data class DeletedItem(
    val id: Long,
    val name: String,
    val type: DeletedItemType,
    val deletedAt: Long,
)

interface TrashRepository {
    fun observeDeletedItems(): Flow<List<DeletedItem>>

    suspend fun restoreItem(itemId: Long, type: DeletedItemType)

    suspend fun permanentlyDeleteItem(itemId: Long, type: DeletedItemType)

    suspend fun emptyVault()

    suspend fun purgeItemsDeletedBefore(cutoffTimeMillis: Long)
}
