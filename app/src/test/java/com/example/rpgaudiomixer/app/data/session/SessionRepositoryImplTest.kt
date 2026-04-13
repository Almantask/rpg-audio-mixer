package com.example.rpgaudiomixer.app.data.session

import com.example.rpgaudiomixer.app.data.local.dao.SessionDao
import com.example.rpgaudiomixer.app.data.local.entities.SessionEntity
import com.example.rpgaudiomixer.app.domain.model.Session
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

class SessionRepositoryImplTest {

    private val mockDao: SessionDao = mockk()
    private val sut = SessionRepositoryImpl(mockDao)

    @Test
    fun `observeByCampaign maps entities to domain models`() = runTest {
        // Arrange
        val entity = SessionEntity(id = 1, campaignId = 10, name = "Session One", createdAt = 1000L)
        every { mockDao.observeByCampaign(10L) } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeByCampaign(10L).first()

        // Assert
        assertThat(result).hasSize(1)
        val session = result[0]
        assertThat(session.id).isEqualTo(1)
        assertThat(session.campaignId).isEqualTo(10)
        assertThat(session.name).isEqualTo("Session One")
        assertThat(session.createdAt).isEqualTo(1000L)
    }

    @Test
    fun `observeByCampaign maps multiple entities`() = runTest {
        // Arrange
        val entities = listOf(
            SessionEntity(id = 1, campaignId = 5, name = "Combat", createdAt = 1000L),
            SessionEntity(id = 2, campaignId = 5, name = "Tavern", createdAt = 2000L)
        )
        every { mockDao.observeByCampaign(5L) } returns flowOf(entities)

        // Act
        val result = sut.observeByCampaign(5L).first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("Combat")
        assertThat(result[1].name).isEqualTo("Tavern")
    }

    @Test
    fun `createSession upserts entity with correct campaignId and name`() = runTest {
        // Arrange
        val entitySlot = slot<SessionEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 1L

        // Act
        sut.createSession(campaignId = 10L, name = "Boss Fight")

        // Assert
        coVerify { mockDao.upsert(any()) }
        assertThat(entitySlot.captured.campaignId).isEqualTo(10L)
        assertThat(entitySlot.captured.name).isEqualTo("Boss Fight")
    }

    @Test
    fun `deleteSession performs soft delete by session id`() = runTest {
        // Arrange
        coEvery { mockDao.softDelete(99L, any()) } just Runs
        val session = Session(id = 99, campaignId = 5, name = "Ancient Battle")

        // Act
        sut.deleteSession(session)

        // Assert
        coVerify { mockDao.softDelete(99L, any()) }
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
