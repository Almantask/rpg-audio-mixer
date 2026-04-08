package com.example.rpgaudiomixer.domain.repository

interface SessionSceneRepository {
    suspend fun getScenesBySession(sessionId: String): List<String> // Returns scene IDs
    suspend fun linkSceneToSession(sessionId: String, sceneId: String)
    suspend fun unlinkSceneFromSession(sessionId: String, sceneId: String)
}
