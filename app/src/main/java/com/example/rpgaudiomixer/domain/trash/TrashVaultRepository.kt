package com.example.rpgaudiomixer.domain.trash

import kotlinx.coroutines.flow.Flow

enum class TrashItemType(
    val label: String,
) {
    CAMPAIGN("Campaign"),
    SESSION("Session"),
    SCENE("Scene"),
    SOUNDSCAPE("Soundscape"),
    FX("FX"),
}

data class TrashItem(
    val key: String,
    val name: String,
    val type: TrashItemType,
    val deletedAtMillis: Long,
)

interface TrashVaultRepository {
    fun observeItems(): Flow<List<TrashItem>>
    suspend fun trashCampaign(campaignId: Long)
    suspend fun trashSession(sessionId: Long)
    suspend fun trashScene(sceneId: Long)
    suspend fun trashSoundscapeCategory(categoryId: Long)
    suspend fun trashFxTrack(trackId: Long)
    suspend fun restore(itemKey: String)
    suspend fun permanentlyDelete(itemKey: String)
    suspend fun emptyVault()
    suspend fun purgeExpired(nowMillis: Long)
    fun reset()
}
