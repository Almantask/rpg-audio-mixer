package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import io.mockk.coEvery
import io.mockk.mockk

class SoundscapeCategoryRepositoryImplTest {
    private val dao: SoundscapeCategoryDao = mockk(relaxed = true)
    private val repository = SoundscapeCategoryRepositoryImpl(dao)

    @Test
    fun `observeAll returns mapped categories`() = runTest {
        // Arrange
        val entities = listOf(SoundscapeCategoryEntity(1, "Cat1"))
        coEvery { dao.observeAll() } returns flowOf(entities)

        // Act
        val result = repository.observeAll()

        // Assert
        assertThat(result).isNotNull()
    }

    @Test
    fun `upsert delegates to dao`() = runTest {
        // Arrange
        val category = SoundscapeCategory(1, "Cat1", emptyList())
        coEvery { dao.upsert(any()) } returns 1L

        // Act
        val id = repository.upsert(category)

        // Assert
        assertThat(id).isEqualTo(1L)
    }
}
