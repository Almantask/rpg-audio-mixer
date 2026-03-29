package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.CampaignDao
import com.example.rpgaudiomixer.data.local.CampaignEntity
import com.example.rpgaudiomixer.domain.model.Campaign
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import io.mockk.coEvery
import io.mockk.mockk

class CampaignRepositoryImplTest {
    private val dao: CampaignDao = mockk(relaxed = true)
    private val repository = CampaignRepositoryImpl(dao)

    @Test
    fun `observeAll returns mapped campaigns`() = runTest {
        // Arrange
        val entities = listOf(CampaignEntity(1, "C1", null, 123L))
        coEvery { dao.observeAll() } returns flowOf(entities)

        // Act
        val result = repository.observeAll()

        // Assert
        assertThat(result).isNotNull()
    }

    @Test
    fun `upsert delegates to dao`() = runTest {
        // Arrange
        val campaign = Campaign(1, "C1", null, 123L)
        coEvery { dao.upsert(any()) } returns 1L

        // Act
        val id = repository.upsert(campaign)

        // Assert
        assertThat(id).isEqualTo(1L)
    }
}
