package com.example.rpgaudiomixer.app.data.session

import com.example.rpgaudiomixer.app.data.local.dao.SessionDao
import com.example.rpgaudiomixer.app.data.local.entities.SessionEntity
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
        val entity = SessionEntity(
            id = 1, campaignId = 10, name = "Tavern Ambience",
            coverArtUri = "content://img/tavern", date = 1000L,
            isDeleted = false, deletedAt = null
        )
        every { mockDao.observeByCampaign(10) } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeByCampaign(10).first()

        // Assert
        assertThat(result).hasSize(1)
        val session = result[0]
        assertThat(session.id).isEqualTo(1)
        assertThat(session.campaignId).isEqualTo(10)
        assertThat(session.name).isEqualTo("Tavern Ambience")
        assertThat(session.coverArtUri).isEqualTo("content://img/tavern")
        assertThat(session.date).isEqualTo(1000L)
        assertThat(session.isDeleted).isFalse()
        assertThat(session.deletedAt).isNull()
    }

    @Test
    fun `observeByCampaign maps multiple entities`() = runTest {
        // Arrange
        val entities = listOf(
            SessionEntity(
                id = 1, campaignId = 10, name = "Forest Walk",
                coverArtUri = null, date = 2000L
            ),
            SessionEntity(
                id = 2, campaignId = 10, name = "Castle Siege",
                coverArtUri = "content://img/castle", date = 3000L
            )
        )
        every { mockDao.observeByCampaign(10) } returns flowOf(entities)

        // Act
        val result = sut.observeByCampaign(10).first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("Forest Walk")
        assertThat(result[1].name).isEqualTo("Castle Siege")
        assertThat(result[1].coverArtUri).isEqualTo("content://img/castle")
    }

    @Test
    fun `observeByCampaign returns empty list when no sessions exist`() = runTest {
        // Arrange
        every { mockDao.observeByCampaign(99) } returns flowOf(emptyList())

        // Act
        val result = sut.observeByCampaign(99).first()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun `observeDeleted maps deleted entities to domain models`() = runTest {
        // Arrange
        val entity = SessionEntity(
            id = 3, campaignId = 10, name = "Archived Session",
            coverArtUri = null, date = 4000L,
            isDeleted = true, deletedAt = 5000L
        )
        every { mockDao.observeDeleted() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeDeleted().first()

        // Assert
        assertThat(result).hasSize(1)
        val session = result[0]
        assertThat(session.id).isEqualTo(3)
        assertThat(session.name).isEqualTo("Archived Session")
        assertThat(session.isDeleted).isTrue()
        assertThat(session.deletedAt).isEqualTo(5000L)
    }

    @Test
    fun `createSession upserts entity and returns id`() = runTest {
        // Arrange
        val entitySlot = slot<SessionEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 7L

        // Act
        val result = sut.createSession(
            campaignId = 10, name = "Dragon Lair", coverArtUri = "content://img/dragon", date = 6000L
        )

        // Assert
        assertThat(result).isEqualTo(7L)
        coVerify { mockDao.upsert(any()) }
        assertThat(entitySlot.captured.campaignId).isEqualTo(10)
        assertThat(entitySlot.captured.name).isEqualTo("Dragon Lair")
        assertThat(entitySlot.captured.coverArtUri).isEqualTo("content://img/dragon")
        assertThat(entitySlot.captured.date).isEqualTo(6000L)
    }

    @Test
    fun `createSession with null coverArtUri upserts with null`() = runTest {
        // Arrange
        val entitySlot = slot<SessionEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 8L

        // Act
        sut.createSession(campaignId = 10, name = "Silent Dungeon", date = 7000L)

        // Assert
        assertThat(entitySlot.captured.coverArtUri).isNull()
    }

    @Test
    fun `softDelete delegates to dao softDelete`() = runTest {
        // Arrange
        coEvery { mockDao.softDelete(any()) } just Runs

        // Act
        sut.softDelete(42)

        // Assert
        coVerify { mockDao.softDelete(42) }
    }

    @Test
    fun `restore delegates to dao restore`() = runTest {
        // Arrange
        coEvery { mockDao.restore(any()) } just Runs

        // Act
        sut.restore(42)

        // Assert
        coVerify { mockDao.restore(42) }
    }

    @Test
    fun `permanentlyDelete delegates to dao permanentlyDelete`() = runTest {
        // Arrange
        coEvery { mockDao.permanentlyDelete(any()) } just Runs

        // Act
        sut.permanentlyDelete(42)

        // Assert
        coVerify { mockDao.permanentlyDelete(42) }
    }

    @Test
    fun `softDeleteByCampaign delegates to dao softDeleteByCampaign`() = runTest {
        // Arrange
        coEvery { mockDao.softDeleteByCampaign(any()) } just Runs

        // Act
        sut.softDeleteByCampaign(10)

        // Assert
        coVerify { mockDao.softDeleteByCampaign(10) }
    }

    @Test
    fun `restoreByCampaign delegates to dao restoreByCampaign`() = runTest {
        // Arrange
        coEvery { mockDao.restoreByCampaign(any()) } just Runs

        // Act
        sut.restoreByCampaign(10)

        // Assert
        coVerify { mockDao.restoreByCampaign(10) }
    }

    @Test
    fun `deleteAll delegates to dao deleteAll`() = runTest {
        // Arrange
        coEvery { mockDao.deleteAll() } just Runs

        // Act
        sut.deleteAll()

        // Assert
        coVerify { mockDao.deleteAll() }
    }
}
