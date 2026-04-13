package com.example.rpgaudiomixer.app.data.trash

import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrashPurgeManager @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository
) {
    suspend fun purgeExpired() {
        val cutoff = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        campaignRepository.purgeOlderThan(cutoff)
        sessionRepository.purgeOlderThan(cutoff)
        sceneRepository.purgeOlderThan(cutoff)
    }
}
