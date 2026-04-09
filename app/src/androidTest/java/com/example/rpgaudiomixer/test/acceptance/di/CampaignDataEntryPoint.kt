package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.trash.CampaignTrashRepository
import com.example.rpgaudiomixer.ui.campaigns.CampaignCoverArtSelectionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CampaignDataEntryPoint {
    fun campaignRepository(): CampaignRepository
    fun campaignTrashRepository(): CampaignTrashRepository
    fun campaignCoverArtSelectionRepository(): CampaignCoverArtSelectionRepository
}
