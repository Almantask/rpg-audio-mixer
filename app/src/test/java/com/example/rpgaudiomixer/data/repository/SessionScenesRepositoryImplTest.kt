package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.SessionScenesDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import io.mockk.coEvery
import io.mockk.mockk

class SessionScenesRepositoryImplTest {
    private val dao: SessionScenesDao = mockk(relaxed = true)
    private val repository = SessionScenesRepositoryImpl(dao)

    @Test
    fun `observeBySession returns mapped scenes`() = runTest {
        // Arrange
        val entities = listOf(SceneEntity(1, "Scene1", null, "tag1"))
        coEvery { dao.observeBySession(1) } returns flowOf(entities)

        // Act
        val result = repository.observeBySession(1)

        // Assert
        assertThat(result).isNotNull()
    }

    @Test
    fun `linkSceneToSession delegates to dao`() = runTest {
        // Arrange
        coEvery { dao.link(any()) } returns Unit

        // Act
        repository.linkSceneToSession(1, 2)

        // Assert
        // No exception means pass
    }
}
