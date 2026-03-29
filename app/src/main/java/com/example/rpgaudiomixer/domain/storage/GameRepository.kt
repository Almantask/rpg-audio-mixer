package com.example.rpgaudiomixer.domain.storage

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.model.SoundEffect
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

interface GameRepository {
    // Campaign
    fun getAllCampaigns(): List<Campaign>
    fun addCampaign(campaign: Campaign)
    fun updateCampaign(campaign: Campaign)
    fun getCampaignById(campaignId: String): Campaign?
    fun getActiveCampaign(): Campaign?

    // Session
    fun getSessionsForCampaign(campaignId: String): List<Session>
    fun addSession(session: Session)

    // Scenes
    fun getAllScenes(): List<Scene>
    fun addScene(scene: Scene)
    fun updateScene(scene: Scene)

    // Soundscapes
    fun getAllSoundscapeCategories(): List<SoundscapeCategory>
    fun addSoundscapeCategory(category: SoundscapeCategory)
    fun updateSoundscapeCategory(category: SoundscapeCategory)

    // FX
    fun getAllSoundEffects(): List<SoundEffect>
    fun addSoundEffect(effect: SoundEffect)
    fun updateSoundEffect(effect: SoundEffect)
}
