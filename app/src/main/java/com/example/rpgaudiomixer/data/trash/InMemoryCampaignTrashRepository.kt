package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.domain.trash.CampaignTrashRepository
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryCampaignTrashRepository @Inject constructor() : CampaignTrashRepository {
    private val deletedCampaigns = CopyOnWriteArraySet<String>()

    override fun recordDeletedCampaign(name: String) {
        deletedCampaigns += name
    }

    override fun containsDeletedCampaign(name: String): Boolean = name in deletedCampaigns

    override fun reset() {
        deletedCampaigns.clear()
    }
}
