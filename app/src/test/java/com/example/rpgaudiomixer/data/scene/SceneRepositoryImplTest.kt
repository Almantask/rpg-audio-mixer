package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.data.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.Scene
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneRepositoryImplTest {

    private val sceneDao: SceneDao = mockk(relaxed = true)
    private val sessionSceneDao: SessionSceneDao = mockk(relaxed = true)
    private val repository = SceneRepositoryImpl(sceneDao, sessionSceneDao)

    @Test
    fun `observeAll returns mapped domain models from DAO`() = runTest {
        // Arrange
        val entities = listOf(
            SceneEntity(id = 1, name = "Scene 1", description = "Desc 1", tags = "tag1,tag2"),
            SceneEntity(id = 2, name = "Scene 2", description = null, tags = "")
        )
        every { sceneDao.observeAll() } returns flowOf(entities)

        // Act
        val result = repository.observeAll().first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0]).isEqualTo(Scene(1, "Scene 1", "Desc 1", listOf("tag1", "tag2")))
        assertThat(result[1]).isEqualTo(Scene(2, "Scene 2", null, emptyList()))
    }

    @Test
    fun `observeBySession returns scenes linked to session`() = runTest {
        // Arrange
        val sessionId = 10L
        val entities = listOf(
            SceneEntity(id = 1, name = "Scene 1", description = null, tags = "combat"),
            SceneEntity(id = 2, name = "Scene 2", description = null, tags = "")
        )
        every { sessionSceneDao.observeScenesBySession(sessionId) } returns flowOf(entities)

        // Act
        val result = repository.observeBySession(sessionId).first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("Scene 1")
        assertThat(result[1].name).isEqualTo("Scene 2")
    }

    @Test
    fun `getById returns domain model when entity exists`() = runTest {
        // Arrange
        val entity = SceneEntity(id = 1, name = "Test Scene", description = "Desc", tags = "tag1,tag2")
        coEvery { sceneDao.getById(1) } returns entity

        // Act
        val result = repository.getById(1)

        // Assert
        assertThat(result).isEqualTo(Scene(1, "Test Scene", "Desc", listOf("tag1", "tag2")))
    }

    @Test
    fun `getById returns null when entity does not exist`() = runTest {
        // Arrange
        coEvery { sceneDao.getById(999) } returns null

        // Act
        val result = repository.getById(999)

        // Assert
        assertThat(result).isNull()
    }

    @Test
    fun `create inserts new scene entity and returns id`() = runTest {
        // Arrange
        coEvery { sceneDao.upsert(any()) } returns 42L

        // Act
        val id = repository.create("New Scene", "Description", listOf("tag1", "tag2"))

        // Assert
        assertThat(id).isEqualTo(42L)
        coVerify {
            sceneDao.upsert(match {
                it.name == "New Scene" && it.description == "Description" && it.tags == "tag1,tag2"
            })
        }
    }

    @Test
    fun `update upserts the scene entity`() = runTest {
        // Arrange
        val scene = Scene(id = 5, name = "Updated", description = "New desc", tags = listOf("a", "b"))

        // Act
        repository.update(scene)

        // Assert
        coVerify {
            sceneDao.upsert(
                SceneEntity(5, "Updated", "New desc", "a,b")
            )
        }
    }

    @Test
    fun `delete calls deleteById on DAO`() = runTest {
        // Arrange
        val sceneId = 10L

        // Act
        repository.delete(sceneId)

        // Assert
        coVerify { sceneDao.deleteById(10L) }
    }

    @Test
    fun `linkToSession inserts cross reference`() = runTest {
        // Arrange
        val sessionId = 5L
        val sceneId = 10L

        // Act
        repository.linkToSession(sessionId, sceneId)

        // Assert
        coVerify {
            sessionSceneDao.link(SessionSceneCrossRef(5L, 10L))
        }
    }

    @Test
    fun `unlinkFromSession removes cross reference`() = runTest {
        // Arrange
        val sessionId = 5L
        val sceneId = 10L

        // Act
        repository.unlinkFromSession(sessionId, sceneId)

        // Assert
        coVerify {
            sessionSceneDao.unlink(5L, 10L)
        }
    }

    @Test
    fun `repository handles empty tags correctly`() = runTest {
        // Arrange
        val entity = SceneEntity(id = 1, name = "Scene", description = null, tags = "")
        coEvery { sceneDao.getById(1) } returns entity

        // Act
        val result = repository.getById(1)

        // Assert
        assertThat(result?.tags).isEmpty()
    }

    @Test
    fun `repository trims whitespace from tags`() = runTest {
        // Arrange
        val entity = SceneEntity(id = 1, name = "Scene", description = null, tags = " tag1 , tag2 , tag3 ")
        coEvery { sceneDao.getById(1) } returns entity

        // Act
        val result = repository.getById(1)

        // Assert
        assertThat(result?.tags).containsExactly("tag1", "tag2", "tag3")
    }
}
