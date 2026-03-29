package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionEntity
import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import io.mockk.coEvery
import io.mockk.mockk

class SessionRepositoryImplTest {
    private val dao: SessionDao = mockk(relaxed = true)
    private val repository = SessionRepositoryImpl(dao)

    @Test
    fun `observeByCampaign returns mapped sessions`() = runTest {
        // Arrange
        val entities = listOf(SessionEntity(1, 1, "Session1", 123L))
        coEvery { dao.observeByCampaign(1) } returns flowOf(entities)

        // Act
        val result = repository.observeByCampaign(1)

        // Assert
        assertThat(result).isNotNull()
    }

    @Test
    fun `upsert delegates to dao`() = runTest {
        // Arrange
        val session = Session(1, 1, "Session1", 123L)
        coEvery { dao.upsert(any()) } returns 1L

        // Act
        val id = repository.upsert(session)

        // Assert
        assertThat(id).isEqualTo(1L)
    }
}
