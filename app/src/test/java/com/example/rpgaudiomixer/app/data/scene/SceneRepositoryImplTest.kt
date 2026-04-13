package com.example.rpgaudiomixer.app.data.scene

import com.example.rpgaudiomixer.app.data.local.dao.SceneDao
import com.example.rpgaudiomixer.app.data.local.entities.SceneEntity
import com.example.rpgaudiomixer.app.data.local.entities.SessionSceneCrossRef
import com.example.rpgaudiomixer.app.domain.model.Scene
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

    private val mockDao: SceneDao = mockk()
    private val sut = SceneRepositoryImpl(mockDao)

    @Test
    fun `observeAll maps entities to domain models`() = runTest {
        // Arrange
        val entity = SceneEntity(id = 1, name = "Tavern", createdAt = 1000L)
        every { mockDao.observeAll() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(1)
        assertThat(result[0].name).isEqualTo("Tavern")
        assertThat(result[0].createdAt).isEqualTo(1000L)
    }

    @Test
    fun `observeBySession maps entities to domain models`() = runTest {
        // Arrange
        val entity = SceneEntity(id = 2, name = "Dungeon", createdAt = 2000L)
        every { mockDao.observeBySession(42L) } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeBySession(42L).first()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(2)
        assertThat(result[0].name).isEqualTo("Dungeon")
        assertThat(result[0].createdAt).isEqualTo(2000L)
    }

    @Test
    fun `createScene upserts entity to dao`() = runTest {
        // Arrange
        val entitySlot = slot<SceneEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 1L

        // Act
        sut.createScene("Forest Battle")

        // Assert
        coVerify { mockDao.upsert(any()) }
        assertThat(entitySlot.captured.name).isEqualTo("Forest Battle")
    }

    @Test
    fun `deleteScene calls softDelete on dao`() = runTest {
        // Arrange
        coEvery { mockDao.softDelete(7L) } just Runs
        val scene = Scene(id = 7, name = "Ancient Lair")

        // Act
        sut.deleteScene(scene)

        // Assert
        coVerify { mockDao.softDelete(7L) }
    }

    @Test
    fun `linkToSession inserts cross ref`() = runTest {
        // Arrange
        val crossRefSlot = slot<SessionSceneCrossRef>()
        coEvery { mockDao.linkSceneToSession(capture(crossRefSlot)) } just Runs

        // Act
        sut.linkToSession(sceneId = 3L, sessionId = 10L)

        // Assert
        coVerify { mockDao.linkSceneToSession(any()) }
        assertThat(crossRefSlot.captured.sceneId).isEqualTo(3L)
        assertThat(crossRefSlot.captured.sessionId).isEqualTo(10L)
    }

    @Test
    fun `unlinkFromSession calls dao`() = runTest {
        // Arrange
        coEvery { mockDao.unlinkSceneFromSession(sessionId = 10L, sceneId = 3L) } just Runs

        // Act
        sut.unlinkFromSession(sceneId = 3L, sessionId = 10L)

        // Assert
        coVerify { mockDao.unlinkSceneFromSession(sessionId = 10L, sceneId = 3L) }
    }

    @Test
    fun `deleteAll calls both deleteAll and deleteAllCrossRefs`() = runTest {
        // Arrange
        coEvery { mockDao.deleteAll() } just Runs
        coEvery { mockDao.deleteAllCrossRefs() } just Runs

        // Act
        sut.deleteAll()

        // Assert
        coVerify { mockDao.deleteAll() }
        coVerify { mockDao.deleteAllCrossRefs() }
    }
}
