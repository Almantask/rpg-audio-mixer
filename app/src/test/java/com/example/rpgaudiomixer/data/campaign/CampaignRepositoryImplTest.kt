package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.data.local.CampaignDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CampaignRepositoryImplTest {

    private val campaignDao: CampaignDao = mockk()
    private val repository = CampaignRepositoryImpl(campaignDao)

    @Test
    fun deleteCampaign_soft_deletes_the_campaign() = runTest {
        // Arrange
        coEvery { campaignDao.softDeleteById(7L, 500L) } returns Unit

        // Act
        repository.deleteCampaign(campaignId = 7L, deletedAtMillis = 500L)

        // Assert
        coVerify(exactly = 1) { campaignDao.softDeleteById(7L, 500L) }
    }
}
