package com.example.rpgaudiomixer.domain.repository

import com.example.rpgaudiomixer.domain.model.Scene

interface SceneRepository {
    suspend fun getLastOpenedSceneInCampaign(campaignId: String): Scene?
    suspend fun getSceneById(id: String): Scene?
}
