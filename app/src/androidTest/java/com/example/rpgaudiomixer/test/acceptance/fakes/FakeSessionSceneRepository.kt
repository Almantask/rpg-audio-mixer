package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.repository.SessionSceneRepository

class FakeSessionSceneRepository : SessionSceneRepository {

    private val sessionScenes = mutableMapOf<String, MutableList<String>>()

    fun clear() {
        sessionScenes.clear()
    }

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
