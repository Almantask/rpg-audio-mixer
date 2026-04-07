package com.example.rpgaudiomixer.data.campaign

import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.domain.model.Campaign
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CampaignRepositoryImplTest {

    private val campaignDao: CampaignDao = mockk(relaxed = true)
    private val repository = CampaignRepositoryImpl(campaignDao)

    @Test
    fun `observeAll returns mapped domain models from DAO`() = runTest {
        // Arrange
        val entities = listOf(
            CampaignEntity(id = 1, name = "Campaign 1", coverArtUri = "uri1", lastPlayedAt = 1000),
            CampaignEntity(id = 2, name = "Campaign 2", coverArtUri = null, lastPlayedAt = 2000)
        )
        every { campaignDao.observeAll() } returns flowOf(entities)

        // Act
        val result = repository.observeAll().first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0]).isEqualTo(Campaign(1, "Campaign 1", "uri1", 1000))
        assertThat(result[1]).isEqualTo(Campaign(2, "Campaign 2", null, 2000))
    }

    @Test
    fun `getById returns domain model when entity exists`() = runTest {
        // Arrange
        val entity = CampaignEntity(id = 1, name = "Test Campaign", coverArtUri = "uri", lastPlayedAt = 1000)
        coEvery { campaignDao.getById(1) } returns entity

        // Act
        val result = repository.getById(1)

        // Assert
        assertThat(result).isEqualTo(Campaign(1, "Test Campaign", "uri", 1000))
    }

    @Test
    fun `getById returns null when entity does not exist`() = runTest {
        // Arrange
        coEvery { campaignDao.getById(999) } returns null

        // Act
        val result = repository.getById(999)

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun `create inserts new campaign entity and returns id`() = runTest {
        // Arrange
        coEvery { campaignDao.upsert(any()) } returns 42L

        // Act
        val id = repository.create("New Campaign", "cover_uri")

        // Assert
        assertThat(id).isEqualTo(42L)
        coVerify {
            campaignDao.upsert(match {
                it.name == "New Campaign" && it.coverArtUri == "cover_uri"
            })
        }
    }

    @Test
    fun `update upserts the campaign entity`() = runTest {
        // Arrange
        val campaign = Campaign(id = 5, name = "Updated", coverArtUri = "new_uri", lastPlayedAt = 5000)

        // Act
        repository.update(campaign)

        // Assert
        coVerify {
            campaignDao.upsert(
                CampaignEntity(5, "Updated", "new_uri", 5000)
            )
        }
    }

    @Test
    fun `delete calls deleteById on DAO`() = runTest {
        // Arrange
        val campaignId = 10L

        // Act
        repository.delete(campaignId)

        // Assert
        coVerify { campaignDao.deleteById(10L) }
    }
}
