package com.example.rpgaudiomixer.infra.storage

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.model.SoundEffect
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.storage.GameRepository

class InMemoryGameRepository : GameRepository {
    private val campaigns = mutableListOf<Campaign>()
    private val sessions = mutableListOf<Session>()
    private val scenes = mutableListOf<Scene>()
    private val soundscapeCategories = mutableListOf<SoundscapeCategory>()
    private val soundEffects = mutableListOf<SoundEffect>()

    override fun getAllCampaigns(): List<Campaign> = campaigns.sortedByDescending { it.lastPlayedAt }

    override fun addCampaign(campaign: Campaign) {
        campaigns.removeAll { it.id == campaign.id }
        campaigns.add(campaign)
    }

    override fun updateCampaign(campaign: Campaign) {
        addCampaign(campaign)
    }

    override fun getCampaignById(campaignId: String): Campaign? = campaigns.firstOrNull { it.id == campaignId }

    override fun getActiveCampaign(): Campaign? = getAllCampaigns().firstOrNull()

    override fun getSessionsForCampaign(campaignId: String): List<Session> =
        sessions.filter { it.campaignId == campaignId }.sortedByDescending { it.lastPlayedAt }

    override fun addSession(session: Session) {
        sessions.removeAll { it.id == session.id }
        sessions.add(session)
    }

    override fun getAllScenes(): List<Scene> = scenes.sortedByDescending { it.lastPlayedAt }

    override fun addScene(scene: Scene) {
        scenes.removeAll { it.id == scene.id }
        scenes.add(scene)
    }

    override fun updateScene(scene: Scene) {
        addScene(scene)
    }

    override fun getAllSoundscapeCategories(): List<SoundscapeCategory> = soundscapeCategories.sortedByDescending { it.playCount }

    override fun addSoundscapeCategory(category: SoundscapeCategory) {
        soundscapeCategories.removeAll { it.id == category.id }
        soundscapeCategories.add(category)
    }

    override fun updateSoundscapeCategory(category: SoundscapeCategory) {
        addSoundscapeCategory(category)
    }

    override fun getAllSoundEffects(): List<SoundEffect> = soundEffects.sortedByDescending { it.playCount }

    override fun addSoundEffect(effect: SoundEffect) {
        soundEffects.removeAll { it.id == effect.id }
        soundEffects.add(effect)
    }

    override fun updateSoundEffect(effect: SoundEffect) {
        addSoundEffect(effect)
    }
}
