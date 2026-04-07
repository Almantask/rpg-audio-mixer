package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionEntity
import com.example.rpgaudiomixer.domain.model.Session
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SessionRepositoryImplTest {

    private val sessionDao: SessionDao = mockk(relaxed = true)
    private val repository = SessionRepositoryImpl(sessionDao)

    @Test
    fun `observeByCampaign returns mapped domain models from DAO`() = runTest {
        // Arrange
        val campaignId = 1L
        val entities = listOf(
            SessionEntity(id = 1, campaignId = campaignId, name = "Session 1", date = 1000, coverArtUri = "uri1"),
            SessionEntity(id = 2, campaignId = campaignId, name = "Session 2", date = 2000, coverArtUri = null)
        )
        every { sessionDao.observeByCampaign(campaignId) } returns flowOf(entities)

        // Act
        val result = repository.observeByCampaign(campaignId).first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0]).isEqualTo(Session(1, campaignId, "Session 1", 1000, "uri1"))
        assertThat(result[1]).isEqualTo(Session(2, campaignId, "Session 2", 2000, null))
    }

    @Test
    fun `getById returns domain model when entity exists`() = runTest {
        // Arrange
        val entity = SessionEntity(id = 1, campaignId = 10, name = "Test Session", date = 1000, coverArtUri = "uri")
        coEvery { sessionDao.getById(1) } returns entity

        // Act
        val result = repository.getById(1)

        // Assert
        assertThat(result).isEqualTo(Session(1, 10, "Test Session", 1000, "uri"))
    }

    @Test
    fun `getById returns null when entity does not exist`() = runTest {
        // Arrange
        coEvery { sessionDao.getById(999) } returns null

        // Act
        val result = repository.getById(999)

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun `create inserts new session entity and returns id`() = runTest {
        // Arrange
        coEvery { sessionDao.upsert(any()) } returns 42L

        // Act
        val id = repository.create(10L, "New Session", "cover_uri")

        // Assert
        assertThat(id).isEqualTo(42L)
        coVerify {
            sessionDao.upsert(match {
                it.campaignId == 10L && it.name == "New Session" && it.coverArtUri == "cover_uri"
            })
        }
    }

    @Test
    fun `update upserts the session entity`() = runTest {
        // Arrange
        val session = Session(id = 5, campaignId = 10, name = "Updated", date = 5000, coverArtUri = "new_uri")

        // Act
        repository.update(session)

        // Assert
        coVerify {
            sessionDao.upsert(
                SessionEntity(5, 10, "Updated", 5000, "new_uri")
            )
        }
    }

    @Test
    fun `delete calls deleteById on DAO`() = runTest {
        // Arrange
        val sessionId = 10L

        // Act
        repository.delete(sessionId)

        // Assert
        coVerify { sessionDao.deleteById(10L) }
    }
}
