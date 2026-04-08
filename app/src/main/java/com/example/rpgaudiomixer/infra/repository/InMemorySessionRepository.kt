package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySessionRepository @Inject constructor() : SessionRepository {

    private val sessions = mutableListOf<Session>()

    override suspend fun getSessionsByCampaign(campaignId: String): List<Session> {
        return sessions
            .filter { it.campaignId == campaignId }
            .sortedByDescending { it.date }
    }

    override suspend fun getSessionById(id: String): Session? {
        return sessions.firstOrNull { it.id == id }
    }

    override suspend fun createSession(campaignId: String, name: String): Session {
        val session = Session(
            id = UUID.randomUUID().toString(),
            campaignId = campaignId,
            name = name,
            date = Instant.now()
        )
        sessions.add(session)
        return session
    }

    override suspend fun updateSession(session: Session) {
        val index = sessions.indexOfFirst { it.id == session.id }
        if (index != -1) {
            sessions[index] = session
        }
    }

    override suspend fun deleteSession(id: String) {
        sessions.removeIf { it.id == id }
    }
}
