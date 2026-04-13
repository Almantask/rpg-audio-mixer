package com.example.rpgaudiomixer.app.domain.repository

import com.example.rpgaudiomixer.app.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>
    fun observeDeleted(): Flow<List<Session>>
    suspend fun createSession(
        campaignId: Long,
        name: String,
        coverArtUri: String? = null,
        date: Long = System.currentTimeMillis()
    ): Long
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    suspend fun permanentlyDelete(id: Long)
    suspend fun softDeleteByCampaign(campaignId: Long)
    suspend fun restoreByCampaign(campaignId: Long)
    suspend fun deleteAll()
}
