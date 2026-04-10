package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.data.session.local.SessionWithSceneCount
import com.example.rpgaudiomixer.data.scene.local.SceneEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SessionRepositoryImplTest {

    private val sessionDao: SessionDao = mockk()
    private val sessionSceneDao: SessionSceneDao = mockk()
    private val repository = SessionRepositoryImpl(sessionDao, sessionSceneDao)

    @Test
    fun observeSessionsByCampaign_maps_rows_to_domain_models() = runTest {
        // Arrange
        every { sessionDao.observeByCampaign(5L) } returns flowOf(
            listOf(
                SessionWithSceneCount(
                    id = 2L,
                    campaignId = 5L,
                    name = "Session 2",
                    dateMillis = 200L,
                    coverArtUri = "content://session-2",
                    sceneCount = 3,
                ),
                SessionWithSceneCount(
                    id = 1L,
                    campaignId = 5L,
                    name = "Session 1",
                    dateMillis = 100L,
                    coverArtUri = null,
                    sceneCount = 1,
                ),
            ),
        )

        // Act
        val sessions = repository.observeSessionsByCampaign(5L).first()

        // Assert
        assertThat(sessions).containsExactly(
            com.example.rpgaudiomixer.domain.model.Session(
                id = 2L,
                campaignId = 5L,
                name = "Session 2",
                dateMillis = 200L,
                coverArtUri = "content://session-2",
                sceneCount = 3,
            ),
            com.example.rpgaudiomixer.domain.model.Session(
                id = 1L,
                campaignId = 5L,
                name = "Session 1",
                dateMillis = 100L,
                coverArtUri = null,
                sceneCount = 1,
            ),
        )
    }

    @Test
    fun linkScenes_links_every_selected_scene_to_the_session() = runTest {
        // Arrange
        coEvery { sessionSceneDao.linkScene(any(), any()) } returns Unit

        // Act
        repository.linkScenes(sessionId = 3L, sceneIds = listOf(7L, 8L, 9L))

        // Assert
        coVerify(exactly = 1) { sessionSceneDao.linkScene(3L, 7L) }
        coVerify(exactly = 1) { sessionSceneDao.linkScene(3L, 8L) }
        coVerify(exactly = 1) { sessionSceneDao.linkScene(3L, 9L) }
    }

    @Test
    fun observeLastOpenedSceneInCampaign_maps_the_scene_entity_to_a_domain_scene() = runTest {
        // Arrange
        every { sessionDao.observeLastOpenedSceneInCampaign(5L) } returns flowOf(
            SceneEntity(
                id = 8L,
                name = "The Foyer",
                description = "Dusty and cold",
                tags = "haunted,entry",
                masterVolume = 0.7f,
            ),
        )

        // Act
        val result = repository.observeLastOpenedSceneInCampaign(5L).first()

        // Assert
        assertThat(result).isEqualTo(
            com.example.rpgaudiomixer.domain.model.Scene(
                id = 8L,
                name = "The Foyer",
                description = "Dusty and cold",
                tags = listOf("haunted", "entry"),
                masterVolume = 0.7f,
            ),
        )
    }

    @Test
    fun markSceneOpened_updates_the_session_last_opened_fields() = runTest {
        // Arrange
        coEvery { sessionDao.updateLastOpenedScene(3L, 8L, 400L) } returns Unit

        // Act
        repository.markSceneOpened(sessionId = 3L, sceneId = 8L, openedAtMillis = 400L)

        // Assert
        coVerify(exactly = 1) { sessionDao.updateLastOpenedScene(3L, 8L, 400L) }
    }

    @Test
    fun deleteSession_soft_deletes_the_session() = runTest {
        // Arrange
        coEvery { sessionDao.softDeleteById(3L, 600L) } returns Unit

        // Act
        repository.deleteSession(sessionId = 3L, deletedAtMillis = 600L)

        // Assert
        coVerify(exactly = 1) { sessionDao.softDeleteById(3L, 600L) }
    }
}
