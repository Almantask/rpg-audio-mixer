package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Scene

interface SceneRepository {
    suspend fun getLastOpenedSceneInCampaign(campaignId: String): Scene?
    suspend fun getSceneById(id: String): Scene?
    suspend fun getAllScenes(): List<Scene>
    suspend fun createScene(name: String, description: String? = null, tags: List<String> = emptyList()): Scene
    suspend fun updateScene(scene: Scene)
    suspend fun deleteScene(id: String)
}
