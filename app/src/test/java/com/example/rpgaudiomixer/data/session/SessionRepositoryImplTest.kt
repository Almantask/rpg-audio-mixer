package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.data.session.local.SessionDao
import com.example.rpgaudiomixer.data.session.local.SessionSceneDao
import com.example.rpgaudiomixer.data.session.local.SessionWithSceneCount
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
}
