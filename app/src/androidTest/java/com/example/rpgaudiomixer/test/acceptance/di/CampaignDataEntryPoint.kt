package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.trash.SceneTrashRepository
import com.example.rpgaudiomixer.domain.trash.CampaignTrashRepository
import com.example.rpgaudiomixer.domain.trash.SessionTrashRepository
import com.example.rpgaudiomixer.ui.campaigns.CampaignCoverArtSelectionRepository
import com.example.rpgaudiomixer.ui.sessions.SessionCoverArtSelectionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CampaignDataEntryPoint {
    fun campaignRepository(): CampaignRepository
    fun sessionRepository(): SessionRepository
    fun sceneRepository(): SceneRepository
    fun campaignTrashRepository(): CampaignTrashRepository
    fun sessionTrashRepository(): SessionTrashRepository
    fun sceneTrashRepository(): SceneTrashRepository
    fun campaignCoverArtSelectionRepository(): CampaignCoverArtSelectionRepository
    fun sessionCoverArtSelectionRepository(): SessionCoverArtSelectionRepository
}
