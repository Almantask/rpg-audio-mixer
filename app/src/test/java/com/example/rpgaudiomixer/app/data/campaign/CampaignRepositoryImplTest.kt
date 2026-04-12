package com.example.rpgaudiomixer.app.data.campaign

import com.example.rpgaudiomixer.app.data.local.dao.CampaignDao
import com.example.rpgaudiomixer.app.data.local.entities.CampaignEntity
import com.example.rpgaudiomixer.app.domain.model.Campaign
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CampaignRepositoryImplTest {

    private val mockDao: CampaignDao = mockk()
    private val sut = CampaignRepositoryImpl(mockDao)

    @Test
    fun `observeAll maps entities to domain models`() = runBlocking {
        // Arrange
        val entity = CampaignEntity(id = 1, name = "Dark Forest", coverArtUri = null, lastPlayedAt = 1000L)
        every { mockDao.observeAll() } returns flowOf(listOf(entity))

        // Act & Assert
        val results = mutableListOf<List<Campaign>>()
        sut.observeAll().collect { results.add(it) }

        assertThat(results).hasSize(1)
        val campaign = results[0][0]
        assertThat(campaign.id).isEqualTo(1)
        assertThat(campaign.name).isEqualTo("Dark Forest")
        assertThat(campaign.coverArtUri).isNull()
        assertThat(campaign.lastPlayedAt).isEqualTo(1000L)
    }

    @Test
    fun `observeAll maps multiple entities`() = runBlocking {
        // Arrange
        val entities = listOf(
            CampaignEntity(id = 1, name = "Forest", coverArtUri = null, lastPlayedAt = 1000L),
            CampaignEntity(id = 2, name = "Castle", coverArtUri = "content://img/1", lastPlayedAt = 2000L)
        )
        every { mockDao.observeAll() } returns flowOf(entities)

        // Act
        val results = mutableListOf<List<Campaign>>()
        sut.observeAll().collect { results.add(it) }

        // Assert
        assertThat(results[0]).hasSize(2)
        assertThat(results[0][0].name).isEqualTo("Forest")
        assertThat(results[0][1].name).isEqualTo("Castle")
        assertThat(results[0][1].coverArtUri).isEqualTo("content://img/1")
    }

    @Test
    fun `createCampaign upserts entity to dao`() = runBlocking {
        // Arrange
        val entitySlot = slot<CampaignEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 1L

        // Act
        sut.createCampaign("Dragon Keep", "content://art/1")

        // Assert
        coVerify { mockDao.upsert(any()) }
        assertThat(entitySlot.captured.name).isEqualTo("Dragon Keep")
        assertThat(entitySlot.captured.coverArtUri).isEqualTo("content://art/1")
    }

    @Test
    fun `createCampaign with null coverArtUri upserts with null`() = runBlocking {
        // Arrange
        val entitySlot = slot<CampaignEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 1L

        // Act
        sut.createCampaign("Ruined Tower", null)

        // Assert
        assertThat(entitySlot.captured.coverArtUri).isNull()
    }

    @Test
    fun `deleteCampaign converts campaign to entity and deletes from dao`() = runBlocking {
        // Arrange
        val entitySlot = slot<CampaignEntity>()
        coEvery { mockDao.delete(capture(entitySlot)) } just Runs
        val campaign = Campaign(id = 42, name = "Ancient Ruins", coverArtUri = null, lastPlayedAt = 5000L)

        // Act
        sut.deleteCampaign(campaign)

        // Assert
        coVerify { mockDao.delete(any()) }
        assertThat(entitySlot.captured.id).isEqualTo(42)
        assertThat(entitySlot.captured.name).isEqualTo("Ancient Ruins")
        assertThat(entitySlot.captured.lastPlayedAt).isEqualTo(5000L)
    }

    @Test
    fun `deleteAll delegates to dao`() = runBlocking {
        // Arrange
        coEvery { mockDao.deleteAll() } just Runs

        // Act
        sut.deleteAll()

        // Assert
        coVerify { mockDao.deleteAll() }
    }
}
