package com.example.rpgaudiomixer.domain.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeLastSuccessfulSyncAt(): Flow<Long?>

    suspend fun syncPurchasesAndFreeTracks()
}
