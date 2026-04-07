package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository

class InMemorySceneRepository : SceneRepository {
    override suspend fun getLastOpenedSceneInCampaign(campaignId: String): Scene? = null
    override suspend fun getSceneById(id: String): Scene? = null
}
