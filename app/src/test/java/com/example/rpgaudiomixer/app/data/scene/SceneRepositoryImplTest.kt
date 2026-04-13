package com.example.rpgaudiomixer.app.data.scene

import com.example.rpgaudiomixer.app.data.local.dao.SceneDao
import com.example.rpgaudiomixer.app.data.local.dao.SessionSceneDao
import com.example.rpgaudiomixer.app.data.local.entities.SceneEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionSceneCrossRef
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

class SceneRepositoryImplTest {

    private val mockSceneDao: SceneDao = mockk()
    private val mockSessionSceneDao: SessionSceneDao = mockk()
    private val sut = SceneRepositoryImpl(mockSceneDao, mockSessionSceneDao)

    // --- observeAll ---

    @Test
    fun `observeAll maps entities to domain models`() = runTest {
        // Arrange
        val entity = SceneEntity(
            id = 1, name = "Tavern Brawl", description = "A noisy tavern",
            tags = "combat,indoor", isDeleted = false, deletedAt = null, createdAt = 1000L
        )
        every { mockSceneDao.observeAll() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(1)
        val scene = result[0]
        assertThat(scene.id).isEqualTo(1)
        assertThat(scene.name).isEqualTo("Tavern Brawl")
        assertThat(scene.description).isEqualTo("A noisy tavern")
        assertThat(scene.tags).isEqualTo("combat,indoor")
        assertThat(scene.isDeleted).isFalse()
        assertThat(scene.deletedAt).isNull()
        assertThat(scene.createdAt).isEqualTo(1000L)
    }

    @Test
    fun `observeAll maps multiple entities`() = runTest {
        // Arrange
        val entities = listOf(
            SceneEntity(
                id = 1, name = "Forest", description = null,
                tags = null, createdAt = 1000L
            ),
            SceneEntity(
                id = 2, name = "Dungeon", description = "Dark dungeon",
                tags = "underground", createdAt = 2000L
            )
        )
        every { mockSceneDao.observeAll() } returns flowOf(entities)

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("Forest")
        assertThat(result[1].name).isEqualTo("Dungeon")
        assertThat(result[1].description).isEqualTo("Dark dungeon")
    }

    @Test
    fun `observeAll returns empty list when no scenes`() = runTest {
        // Arrange
        every { mockSceneDao.observeAll() } returns flowOf(emptyList())

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).isEmpty()
    }

    // --- observeDeleted ---

    @Test
    fun `observeDeleted maps deleted entities to domain models`() = runTest {
        // Arrange
        val entity = SceneEntity(
            id = 5, name = "Archived Scene", description = null,
            tags = null, isDeleted = true, deletedAt = 4000L, createdAt = 3000L
        )
        every { mockSceneDao.observeDeleted() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeDeleted().first()

        // Assert
        assertThat(result).hasSize(1)
        val scene = result[0]
        assertThat(scene.id).isEqualTo(5)
        assertThat(scene.name).isEqualTo("Archived Scene")
        assertThat(scene.isDeleted).isTrue()
        assertThat(scene.deletedAt).isEqualTo(4000L)
    }

    // --- getById ---

    @Test
    fun `getById returns domain model when entity exists`() = runTest {
        // Arrange
        val entity = SceneEntity(
            id = 42, name = "Boss Battle", description = "Epic fight",
            tags = "boss,combat", createdAt = 5000L
        )
        coEvery { mockSceneDao.getById(42) } returns entity

        // Act
        val result = sut.getById(42)

        // Assert
        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(42)
        assertThat(result?.name).isEqualTo("Boss Battle")
        assertThat(result?.description).isEqualTo("Epic fight")
        assertThat(result?.tags).isEqualTo("boss,combat")
    }

    @Test
    fun `getById returns null when entity does not exist`() = runTest {
        // Arrange
        coEvery { mockSceneDao.getById(999) } returns null

        // Act
        val result = sut.getById(999)

        // Assert
        assertThat(result).isNull()
    }

    // --- createScene ---

    @Test
    fun `createScene upserts entity and returns id`() = runTest {
        // Arrange
        val entitySlot = slot<SceneEntity>()
        coEvery { mockSceneDao.upsert(capture(entitySlot)) } returns 7L

        // Act
        val result = sut.createScene("Dragon Lair", "A hot cave", "dragon,fire")

        // Assert
        assertThat(result).isEqualTo(7L)
        coVerify { mockSceneDao.upsert(any()) }
        assertThat(entitySlot.captured.name).isEqualTo("Dragon Lair")
        assertThat(entitySlot.captured.description).isEqualTo("A hot cave")
        assertThat(entitySlot.captured.tags).isEqualTo("dragon,fire")
        assertThat(entitySlot.captured.id).isEqualTo(0)
    }

    @Test
    fun `createScene with null description and tags`() = runTest {
        // Arrange
        val entitySlot = slot<SceneEntity>()
        coEvery { mockSceneDao.upsert(capture(entitySlot)) } returns 8L

        // Act
        sut.createScene("Simple Scene")

        // Assert
        assertThat(entitySlot.captured.description).isNull()
        assertThat(entitySlot.captured.tags).isNull()
    }

    // --- updateScene ---

    @Test
    fun `updateScene upserts the mapped entity`() = runTest {
        // Arrange
        val entitySlot = slot<SceneEntity>()
        coEvery { mockSceneDao.upsert(capture(entitySlot)) } returns 10L

        // Act
        sut.updateScene(
            com.example.rpgaudiomixer.app.domain.model.Scene(
                id = 10, name = "Updated Scene", description = "New desc",
                tags = "updated", createdAt = 1000L
            )
        )

        // Assert
        coVerify { mockSceneDao.upsert(any()) }
        assertThat(entitySlot.captured.id).isEqualTo(10)
        assertThat(entitySlot.captured.name).isEqualTo("Updated Scene")
        assertThat(entitySlot.captured.description).isEqualTo("New desc")
        assertThat(entitySlot.captured.tags).isEqualTo("updated")
        assertThat(entitySlot.captured.createdAt).isEqualTo(1000L)
    }

    // --- deleteScene ---

    @Test
    fun `deleteScene soft-deletes via dao`() = runTest {
        // Arrange
        coEvery { mockSceneDao.softDelete(any()) } just Runs

        // Act
        sut.deleteScene(42)

        // Assert
        coVerify { mockSceneDao.softDelete(42) }
    }

    // --- restoreScene ---

    @Test
    fun `restoreScene delegates to dao restore`() = runTest {
        // Arrange
        coEvery { mockSceneDao.restore(any()) } just Runs

        // Act
        sut.restoreScene(7)

        // Assert
        coVerify { mockSceneDao.restore(7) }
    }

    // --- permanentlyDeleteScene ---

    @Test
    fun `permanentlyDeleteScene delegates to dao permanentlyDelete`() = runTest {
        // Arrange
        coEvery { mockSceneDao.permanentlyDelete(any()) } just Runs

        // Act
        sut.permanentlyDeleteScene(13)

        // Assert
        coVerify { mockSceneDao.permanentlyDelete(13) }
    }

    // --- cloneScene ---

    @Test
    fun `cloneScene creates copy with prefixed name`() = runTest {
        // Arrange
        val original = SceneEntity(
            id = 1, name = "Forest Ambience", description = "Peaceful forest",
            tags = "nature,outdoor", createdAt = 1000L
        )
        coEvery { mockSceneDao.getById(1) } returns original
        val entitySlot = slot<SceneEntity>()
        coEvery { mockSceneDao.upsert(capture(entitySlot)) } returns 2L

        // Act
        val result = sut.cloneScene(1)

        // Assert
        assertThat(result).isEqualTo(2L)
        assertThat(entitySlot.captured.name).isEqualTo("Copy of Forest Ambience")
        assertThat(entitySlot.captured.description).isEqualTo("Peaceful forest")
        assertThat(entitySlot.captured.tags).isEqualTo("nature,outdoor")
        assertThat(entitySlot.captured.id).isEqualTo(0)
    }

    @Test
    fun `cloneScene throws when scene not found`() = runTest {
        // Arrange
        coEvery { mockSceneDao.getById(999) } returns null

        // Act & Assert
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            sut.cloneScene(999)
        }
        assertThat(exception.message).isEqualTo("Scene with id 999 not found")
    }

    // --- observeScenesForSession ---

    @Test
    fun `observeScenesForSession maps entities to domain models`() = runTest {
        // Arrange
        val entities = listOf(
            SceneEntity(
                id = 1, name = "Battle", description = null,
                tags = null, createdAt = 1000L
            ),
            SceneEntity(
                id = 2, name = "Rest", description = "Campfire rest",
                tags = "rest", createdAt = 2000L
            )
        )
        every { mockSessionSceneDao.observeScenesForSession(10) } returns flowOf(entities)

        // Act
        val result = sut.observeScenesForSession(10).first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("Battle")
        assertThat(result[1].name).isEqualTo("Rest")
        assertThat(result[1].description).isEqualTo("Campfire rest")
    }

    @Test
    fun `observeScenesForSession returns empty list when no scenes linked`() = runTest {
        // Arrange
        every { mockSessionSceneDao.observeScenesForSession(99) } returns flowOf(emptyList())

        // Act
        val result = sut.observeScenesForSession(99).first()

        // Assert
        assertThat(result).isEmpty()
    }

    // --- linkSceneToSession ---

    @Test
    fun `linkSceneToSession delegates to session scene dao`() = runTest {
        // Arrange
        val refSlot = slot<SessionSceneCrossRef>()
        coEvery { mockSessionSceneDao.linkSceneToSession(capture(refSlot)) } just Runs

        // Act
        sut.linkSceneToSession(sessionId = 10, sceneId = 5)

        // Assert
        coVerify { mockSessionSceneDao.linkSceneToSession(any()) }
        assertThat(refSlot.captured.sessionId).isEqualTo(10)
        assertThat(refSlot.captured.sceneId).isEqualTo(5)
    }

    // --- unlinkSceneFromSession ---

    @Test
    fun `unlinkSceneFromSession delegates to session scene dao`() = runTest {
        // Arrange
        coEvery { mockSessionSceneDao.unlinkSceneFromSession(any(), any()) } just Runs

        // Act
        sut.unlinkSceneFromSession(sessionId = 10, sceneId = 5)

        // Assert
        coVerify { mockSessionSceneDao.unlinkSceneFromSession(10, 5) }
    }
}
