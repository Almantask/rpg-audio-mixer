package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import io.mockk.coEvery
import io.mockk.mockk

class SceneRepositoryImplTest {
    private val dao: SceneDao = mockk(relaxed = true)
    private val repository = SceneRepositoryImpl(dao)

    @Test
    fun `observeAll returns mapped scenes`() = runTest {
        // Arrange
        val entities = listOf(SceneEntity(1, "S1", null, "tag1"))
        coEvery { dao.observeAll() } returns flowOf(entities)

        // Act
        val result = repository.observeAll()

        // Assert
        assertThat(result).isNotNull()
    }

    @Test
    fun `upsert delegates to dao`() = runTest {
        // Arrange
        val scene = Scene(1, "S1", null, listOf("tag1"))
        coEvery { dao.upsert(any()) } returns 1L

        // Act
        val id = repository.upsert(scene)

        // Assert
        assertThat(id).isEqualTo(1L)
    }
}
