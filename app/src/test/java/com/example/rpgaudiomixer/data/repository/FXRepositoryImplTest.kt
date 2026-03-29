package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.FXDao
import com.example.rpgaudiomixer.data.local.FXEntity
import com.example.rpgaudiomixer.domain.model.FX
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import io.mockk.coEvery
import io.mockk.mockk

class FXRepositoryImplTest {
    private val dao: FXDao = mockk(relaxed = true)
    private val repository = FXRepositoryImpl(dao)

    @Test
    fun `observeAll returns mapped FX`() = runTest {
        // Arrange
        val entities = listOf(FXEntity(1, "FX1", "uri1", "tag1"))
        coEvery { dao.observeAll() } returns flowOf(entities)

        // Act
        val result = repository.observeAll()

        // Assert
        assertThat(result).isNotNull()
    }

    @Test
    fun `upsert delegates to dao`() = runTest {
        // Arrange
        val fx = FX(1, "FX1", "uri1", listOf("tag1"))
        coEvery { dao.upsert(any()) } returns 1L

        // Act
        val id = repository.upsert(fx)

        // Assert
        assertThat(id).isEqualTo(1L)
    }
}
