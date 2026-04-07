package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeByCampaign(campaignId: Long): Flow<List<Session>>
    suspend fun getById(id: Long): Session?
    suspend fun create(campaignId: Long, name: String, coverUri: String?): Long
    suspend fun delete(id: Long)
}
