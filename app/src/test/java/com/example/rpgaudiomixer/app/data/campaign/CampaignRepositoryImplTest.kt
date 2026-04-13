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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CampaignRepositoryImplTest {

    private val mockDao: CampaignDao = mockk()
    private val sut = CampaignRepositoryImpl(mockDao)

    @Test
    fun `observeAll maps entities to domain models`() = runTest {
        // Arrange
        val entity = CampaignEntity(
            id = 1, name = "Dark Forest", coverArtUri = null,
            lastPlayedAt = 1000L, isDeleted = false, deletedAt = null
        )
        every { mockDao.observeAll() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(1)
        val campaign = result[0]
        assertThat(campaign.id).isEqualTo(1)
        assertThat(campaign.name).isEqualTo("Dark Forest")
        assertThat(campaign.coverArtUri).isNull()
        assertThat(campaign.lastPlayedAt).isEqualTo(1000L)
        assertThat(campaign.isDeleted).isFalse()
        assertThat(campaign.deletedAt).isNull()
    }

    @Test
    fun `observeAll maps multiple entities`() = runTest {
        // Arrange
        val entities = listOf(
            CampaignEntity(
                id = 1, name = "Forest", coverArtUri = null,
                lastPlayedAt = 1000L, isDeleted = false, deletedAt = null
            ),
            CampaignEntity(
                id = 2, name = "Castle", coverArtUri = "content://img/1",
                lastPlayedAt = 2000L, isDeleted = false, deletedAt = null
            )
        )
        every { mockDao.observeAll() } returns flowOf(entities)

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("Forest")
        assertThat(result[1].name).isEqualTo("Castle")
        assertThat(result[1].coverArtUri).isEqualTo("content://img/1")
    }

    @Test
    fun `observeDeleted maps deleted entities to domain models`() = runTest {
        // Arrange
        val entity = CampaignEntity(
            id = 5, name = "Archived Quest", coverArtUri = null,
            lastPlayedAt = 3000L, isDeleted = true, deletedAt = 4000L
        )
        every { mockDao.observeDeleted() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeDeleted().first()

        // Assert
        assertThat(result).hasSize(1)
        val campaign = result[0]
        assertThat(campaign.id).isEqualTo(5)
        assertThat(campaign.name).isEqualTo("Archived Quest")
        assertThat(campaign.isDeleted).isTrue()
        assertThat(campaign.deletedAt).isEqualTo(4000L)
    }

    @Test
    fun `createCampaign upserts entity to dao`() = runTest {
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
    fun `createCampaign with null coverArtUri upserts with null`() = runTest {
        // Arrange
        val entitySlot = slot<CampaignEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 1L

        // Act
        sut.createCampaign("Ruined Tower", null)

        // Assert
        assertThat(entitySlot.captured.coverArtUri).isNull()
    }

    @Test
    fun `deleteCampaign soft-deletes via dao`() = runTest {
        // Arrange
        coEvery { mockDao.softDelete(any()) } just Runs
        val campaignId = 42L

        // Act
        sut.deleteCampaign(campaignId)

        // Assert
        coVerify { mockDao.softDelete(42) }
    }

    @Test
    fun `restoreCampaign delegates to dao restore`() = runTest {
        // Arrange
        coEvery { mockDao.restore(any()) } just Runs

        // Act
        sut.restoreCampaign(7)

        // Assert
        coVerify { mockDao.restore(7) }
    }

    @Test
    fun `permanentlyDeleteCampaign delegates to dao permanentlyDelete`() = runTest {
        // Arrange
        coEvery { mockDao.permanentlyDelete(any()) } just Runs

        // Act
        sut.permanentlyDeleteCampaign(13)

        // Assert
        coVerify { mockDao.permanentlyDelete(13) }
    }

    @Test
    fun `deleteAll delegates to dao`() = runTest {
        // Arrange
        coEvery { mockDao.deleteAll() } just Runs

        // Act
        sut.deleteAll()

        // Assert
        coVerify { mockDao.deleteAll() }
    }
}
