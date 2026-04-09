package com.example.rpgaudiomixer.domain.trash

interface CampaignTrashRepository {
    fun recordDeletedCampaign(name: String)
    fun containsDeletedCampaign(name: String): Boolean
    fun reset()
}
