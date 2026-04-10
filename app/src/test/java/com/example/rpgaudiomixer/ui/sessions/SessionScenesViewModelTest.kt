package com.example.rpgaudiomixer.ui.sessions

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class SessionScenesViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun importScenes_links_selected_scenes_and_removes_them_from_picker_options() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val sceneRepository = FakeSceneRepository(
            scenes = listOf(
                Scene(id = 1L, name = "Tavern", description = null, tags = listOf("social")),
                Scene(id = 2L, name = "Forest", description = null, tags = listOf("outdoors")),
            ),
        )
        val sessionRepository = FakeSessionRepository(
            session = Session(
                id = 5L,
                campaignId = 3L,
                name = "Session 1",
                dateMillis = 100L,
                coverArtUri = null,
                sceneCount = 0,
            ),
        )
        val campaignRepository = FakeCampaignRepository()
        val viewModel = SessionScenesViewModel(
            sessionId = 5L,
            sessionRepository = sessionRepository,
            sceneRepository = sceneRepository,
            campaignRepository = campaignRepository,
        )

        // Act
        advanceUntilIdle()
        viewModel.importScenes(listOf(1L, 2L))
        advanceUntilIdle()

        // Assert
        val successState = viewModel.uiState.value as SessionScenesUiState.Success
        assertThat(successState.linkedScenes.map { it.name }).containsExactly("Tavern", "Forest")
        assertThat(successState.availableScenesToImport).isEmpty()
    }

    @Test
    fun onSceneOpened_records_the_last_opened_scene_for_the_session() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val sceneRepository = FakeSceneRepository(
            scenes = listOf(
                Scene(id = 1L, name = "Tavern", description = null, tags = listOf("social")),
            ),
        )
        val sessionRepository = FakeSessionRepository(
            session = Session(
                id = 5L,
                campaignId = 3L,
                name = "Session 1",
                dateMillis = 100L,
                coverArtUri = null,
                sceneCount = 1,
            ),
        )
        val campaignRepository = FakeCampaignRepository()
        val viewModel = SessionScenesViewModel(
            sessionId = 5L,
            sessionRepository = sessionRepository,
            sceneRepository = sceneRepository,
            campaignRepository = campaignRepository,
        )

        // Act
        advanceUntilIdle()
        viewModel.onSceneOpened(sceneId = 1L)
        advanceUntilIdle()

        // Assert
        assertThat(sessionRepository.lastOpenedSceneId).isEqualTo(1L)
        assertThat(campaignRepository.lastPlayedCampaignId).isEqualTo(3L)
    }

    private class FakeSceneRepository(
        scenes: List<Scene>,
    ) : SceneRepository {
        private val sceneFlow = MutableStateFlow(scenes)

        override fun observeScenes(): Flow<List<Scene>> = sceneFlow

        override fun observeScene(sceneId: Long): Flow<Scene?> {
            return flowOf(sceneFlow.value.firstOrNull { it.id == sceneId })
        }

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long {
            error("Not needed in this test")
        }

        override suspend fun deleteScene(sceneId: Long, deletedAtMillis: Long) {
            error("Not needed in this test")
        }

        override suspend fun updateMasterVolume(sceneId: Long, masterVolume: Float) {
            error("Not needed in this test")
        }
    }

    private class FakeSessionRepository(
        session: Session,
    ) : SessionRepository {
        private val sessionFlow = MutableStateFlow(session)
        private val linkedScenesFlow = MutableStateFlow<List<Scene>>(emptyList())
        var lastOpenedSceneId: Long? = null

        override fun observeSessionsByCampaign(campaignId: Long): Flow<List<Session>> {
            return flowOf(emptyList())
        }

        override fun observeSession(sessionId: Long): Flow<Session?> {
            return sessionFlow
        }

        override fun observeScenesBySession(sessionId: Long): Flow<List<Scene>> {
            return linkedScenesFlow
        }

        override suspend fun createSession(
            campaignId: Long,
            name: String,
            dateMillis: Long,
            coverArtUri: String?,
        ): Long {
            error("Not needed in this test")
        }

        override suspend fun deleteSession(sessionId: Long, deletedAtMillis: Long) {
            error("Not needed in this test")
        }

        override suspend fun linkScenes(sessionId: Long, sceneIds: List<Long>) {
            linkedScenesFlow.value = linkedScenesFlow.value + sceneIds.map { sceneId ->
                Scene(
                    id = sceneId,
                    name = if (sceneId == 1L) "Tavern" else "Forest",
                    description = null,
                    tags = if (sceneId == 1L) listOf("social") else listOf("outdoors"),
                )
            }
            sessionFlow.value = sessionFlow.value.copy(sceneCount = linkedScenesFlow.value.size)
        }

        override suspend fun unlinkScene(sessionId: Long, sceneId: Long) {
            linkedScenesFlow.value = linkedScenesFlow.value.filterNot { it.id == sceneId }
            sessionFlow.value = sessionFlow.value.copy(sceneCount = linkedScenesFlow.value.size)
        }

        override fun observeLastOpenedSceneInCampaign(campaignId: Long): Flow<Scene?> {
            return flowOf(null)
        }

        override suspend fun markSceneOpened(sessionId: Long, sceneId: Long, openedAtMillis: Long) {
            lastOpenedSceneId = sceneId
        }
    }

    private class FakeCampaignRepository : CampaignRepository {
        var lastPlayedCampaignId: Long? = null

        override fun observeCampaigns(): Flow<List<Campaign>> = flowOf(emptyList())

        override fun observeCampaign(campaignId: Long): Flow<Campaign?> = flowOf(null)

        override fun observeActiveCampaign(): Flow<Campaign?> = flowOf(null)

        override suspend fun createCampaign(name: String, coverArtUri: String?): Long {
            error("Not needed in this test")
        }

        override suspend fun deleteCampaign(campaignId: Long, deletedAtMillis: Long) {
            error("Not needed in this test")
        }

        override suspend fun markCampaignPlayed(campaignId: Long, playedAtMillis: Long) {
            lastPlayedCampaignId = campaignId
        }
    }
}
