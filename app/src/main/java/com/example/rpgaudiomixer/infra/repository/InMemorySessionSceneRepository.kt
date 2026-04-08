package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.repository.SessionSceneRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySessionSceneRepository @Inject constructor() : SessionSceneRepository {

    // Map of sessionId to list of sceneIds
    private val sessionScenes = mutableMapOf<String, MutableList<String>>()

    override suspend fun getScenesBySession(sessionId: String): List<String> {
        return sessionScenes[sessionId]?.toList() ?: emptyList()
    }

    override suspend fun linkSceneToSession(sessionId: String, sceneId: String) {
        sessionScenes.getOrPut(sessionId) { mutableListOf() }.add(sceneId)
    }

    override suspend fun unlinkSceneFromSession(sessionId: String, sceneId: String) {
        sessionScenes[sessionId]?.remove(sceneId)
    }
}
