package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Session

interface SessionRepository {
    suspend fun getSessionsByCampaign(campaignId: String): List<Session>
    suspend fun getSessionById(id: String): Session?
    suspend fun createSession(campaignId: String, name: String): Session
    suspend fun updateSession(session: Session)
    suspend fun deleteSession(id: String)
}
