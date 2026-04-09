package com.example.rpgaudiomixer.ui.sessions

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CampaignSessionsViewModelTest {

    @Test
    fun init_exposes_campaign_details_and_sessions() = runTest {
        // Arrange
        val campaignRepository = FakeCampaignRepository()
        val sessionRepository = FakeSessionRepository()
        campaignRepository.emitCampaign(Campaign(7L, "Curse of Strahd", "content://campaign/7", 0L))
        sessionRepository.emitSessions(
            listOf(
                Session(2L, 7L, "Session 2", 200L, null, 3),
                Session(1L, 7L, "Session 1", 100L, null, 1),
            )
        )

        // Act
        val viewModel = CampaignSessionsViewModel(
            campaignId = 7L,
            campaignRepository = campaignRepository,
            sessionRepository = sessionRepository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(
            CampaignSessionsUiState(
                isLoading = false,
                campaign = Campaign(7L, "Curse of Strahd", "content://campaign/7", 0L),
                sessions = listOf(
                    Session(2L, 7L, "Session 2", 200L, null, 3),
                    Session(1L, 7L, "Session 1", 100L, null, 1),
                ),
            )
        )
    }

    @Test
    fun createSession_trims_the_name_and_delegates_to_the_repository() = runTest {
        // Arrange
        val campaignRepository = FakeCampaignRepository()
        val sessionRepository = FakeSessionRepository()
        val viewModel = CampaignSessionsViewModel(
            campaignId = 9L,
            campaignRepository = campaignRepository,
            sessionRepository = sessionRepository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.createSession("  Session 1  ", 1234L, "content://session/1")
        advanceUntilIdle()

        // Assert
        assertThat(sessionRepository.createdRequests).containsExactly(
            CreateSessionRequest(
                campaignId = 9L,
                name = "Session 1",
                date = 1234L,
                coverArtUri = "content://session/1",
            )
        )
    }

    @Test
    fun deleteSession_delegates_to_the_repository() = runTest {
        // Arrange
        val sessionRepository = FakeSessionRepository()
        val viewModel = CampaignSessionsViewModel(
            campaignId = 9L,
            campaignRepository = FakeCampaignRepository(),
            sessionRepository = sessionRepository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.deleteSession(3L)
        advanceUntilIdle()

        // Assert
        assertThat(sessionRepository.deletedSessionIds).containsExactly(3L)
    }

    private class FakeCampaignRepository : CampaignRepository {
        private val campaignsFlow = MutableStateFlow<List<Campaign>>(emptyList())

        fun emitCampaign(campaign: Campaign) {
            campaignsFlow.value = listOf(campaign)
        }

        override fun observeCampaigns(): Flow<List<Campaign>> = campaignsFlow

        override fun observeCampaign(campaignId: Long): Flow<Campaign?> =
            MutableStateFlow(campaignsFlow.value.firstOrNull { it.id == campaignId })

        override suspend fun createCampaign(name: String, coverArtUri: String?): Long = 0L

        override suspend fun deleteCampaign(campaignId: Long) = Unit
    }

    private class FakeSessionRepository : SessionRepository {
        private val sessionsFlow = MutableStateFlow<List<Session>>(emptyList())
        private val sessionFlow = MutableStateFlow<Session?>(null)

        val createdRequests = mutableListOf<CreateSessionRequest>()
        val deletedSessionIds = mutableListOf<Long>()

        fun emitSessions(sessions: List<Session>) {
            sessionsFlow.value = sessions
        }

        override fun observeSessions(campaignId: Long): Flow<List<Session>> = sessionsFlow

        override fun observeSession(sessionId: Long): Flow<Session?> = sessionFlow

        override suspend fun createSession(
            campaignId: Long,
            name: String,
            date: Long,
            coverArtUri: String?,
        ): Long {
            createdRequests += CreateSessionRequest(campaignId, name, date, coverArtUri)
            return createdRequests.size.toLong()
        }

        override suspend fun deleteSession(sessionId: Long) {
            deletedSessionIds += sessionId
        }

        override suspend fun recordOpenedScene(sessionId: Long, sceneId: Long) = Unit
    }

    private data class CreateSessionRequest(
        val campaignId: Long,
        val name: String,
        val date: Long,
        val coverArtUri: String?,
    )
}
