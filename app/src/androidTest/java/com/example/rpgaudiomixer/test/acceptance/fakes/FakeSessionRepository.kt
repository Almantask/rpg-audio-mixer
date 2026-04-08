package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.repository.SessionRepository
import java.time.Instant
import java.util.UUID

class FakeSessionRepository : SessionRepository {

    private val sessions = mutableListOf<Session>()

    fun addSession(session: Session) {
        sessions.add(session)
    }

    fun clear() {
        sessions.clear()
    }

    override suspend fun getSessionsByCampaign(campaignId: String): List<Session> {
        return sessions
            .filter { it.campaignId == campaignId }
            .sortedByDescending { it.date }
    }

    override suspend fun getSessionById(id: String): Session? {
        return sessions.firstOrNull { it.id == id }
    }

    override suspend fun createSession(campaignId: String, name: String): Session {
        val newSession = Session(
            id = UUID.randomUUID().toString(),
            campaignId = campaignId,
            name = name,
            date = Instant.now()
        )
        sessions.add(newSession)
        return newSession
    }

    override suspend fun updateSession(session: Session) {
        val index = sessions.indexOfFirst { it.id == session.id }
        if (index != -1) {
            sessions[index] = session
        }
    }

    override suspend fun deleteSession(id: String) {
        sessions.removeAll { it.id == id }
    }
}
