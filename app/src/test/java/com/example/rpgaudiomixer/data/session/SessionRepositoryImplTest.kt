package com.example.rpgaudiomixer.data.session

import com.example.rpgaudiomixer.data.local.SessionDao
import com.example.rpgaudiomixer.data.local.SessionEntity
import com.example.rpgaudiomixer.data.local.SessionSummaryEntity
import com.example.rpgaudiomixer.domain.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SessionRepositoryImplTest {

    private val dao = FakeSessionDao()
    private val campaignDao = FakeCampaignDao()
    private val repository = SessionRepositoryImpl(
        sessionDao = dao,
        campaignDao = campaignDao,
        currentTimeProvider = { 1_726_000_000_000L },
    )

    @Test
    fun observeSessions_maps_session_summaries_to_domain_models() = runTest {
        // Arrange
        dao.emitSessions(
            listOf(
                SessionSummaryEntity(
                    id = 2L,
                    campaignId = 10L,
                    name = "Session 2",
                    date = 200L,
                    coverArtUri = "content://session/2",
                    sceneCount = 3,
                ),
                SessionSummaryEntity(
                    id = 1L,
                    campaignId = 10L,
                    name = "Session 1",
                    date = 100L,
                    coverArtUri = null,
                    sceneCount = 1,
                ),
            )
        )

        // Act
        val result = repository.observeSessions(campaignId = 10L).first()

        // Assert
        assertThat(result).containsExactly(
            Session(
                id = 2L,
                campaignId = 10L,
                name = "Session 2",
                date = 200L,
                coverArtUri = "content://session/2",
                sceneCount = 3,
            ),
            Session(
                id = 1L,
                campaignId = 10L,
                name = "Session 1",
                date = 100L,
                coverArtUri = null,
                sceneCount = 1,
            ),
        )
    }

    @Test
    fun observeSession_maps_a_single_summary_to_a_domain_model() = runTest {
        // Arrange
        dao.emitSession(
            sessionId = 3L,
            session = SessionSummaryEntity(
                id = 3L,
                campaignId = 10L,
                name = "Session 3",
                date = 300L,
                coverArtUri = null,
                sceneCount = 2,
            ),
        )

        // Act
        val result = repository.observeSession(sessionId = 3L).first()

        // Assert
        assertThat(result).isEqualTo(
            Session(
                id = 3L,
                campaignId = 10L,
                name = "Session 3",
                date = 300L,
                coverArtUri = null,
                sceneCount = 2,
            )
        )
    }

    @Test
    fun createSession_inserts_the_expected_entity() = runTest {
        // Arrange
        val campaignId = 7L

        // Act
        repository.createSession(
            campaignId = campaignId,
            name = "Session 1",
            date = 999L,
            coverArtUri = "content://session/new",
        )

        // Assert
        assertThat(dao.upsertedSessions).containsExactly(
            SessionEntity(
                id = 0L,
                campaignId = campaignId,
                name = "Session 1",
                date = 999L,
                coverArtUri = "content://session/new",
            )
        )
    }

    @Test
    fun deleteSession_deletes_by_id() = runTest {
        // Arrange
        val sessionId = 5L

        // Act
        repository.deleteSession(sessionId)

        // Assert
        assertThat(dao.deletedSessionIds).containsExactly(sessionId)
    }

    @Test
    fun recordOpenedScene_updates_the_session_and_campaign_play_timestamps() = runTest {
        // Arrange

        // Act
        repository.recordOpenedScene(sessionId = 5L, sceneId = 9L)

        // Assert
        assertThat(dao.recordOpenedSceneRequests).containsExactly(Triple(5L, 9L, 1_726_000_000_000L))
        assertThat(campaignDao.recordPlayedForSessionRequests).containsExactly(5L to 1_726_000_000_000L)
    }

    private class FakeSessionDao : SessionDao {
        private val sessionsFlow = MutableStateFlow<List<SessionSummaryEntity>>(emptyList())
        private val sessionFlows = mutableMapOf<Long, MutableStateFlow<SessionSummaryEntity?>>()

        val upsertedSessions = mutableListOf<SessionEntity>()
        val deletedSessionIds = mutableListOf<Long>()
        val recordOpenedSceneRequests = mutableListOf<Triple<Long, Long, Long>>()

        fun emitSessions(sessions: List<SessionSummaryEntity>) {
            sessionsFlow.value = sessions
        }

        fun emitSession(sessionId: Long, session: SessionSummaryEntity?) {
            sessionFlows.getOrPut(sessionId) { MutableStateFlow(null) }.value = session
        }

        override fun observeByCampaign(campaignId: Long): Flow<List<SessionSummaryEntity>> = sessionsFlow

        override fun observeById(sessionId: Long): Flow<SessionSummaryEntity?> =
            sessionFlows.getOrPut(sessionId) { MutableStateFlow(null) }

        override suspend fun upsert(session: SessionEntity): Long {
            upsertedSessions += session
            return session.id
        }

        override suspend fun recordOpenedScene(sessionId: Long, sceneId: Long, openedAt: Long) {
            recordOpenedSceneRequests += Triple(sessionId, sceneId, openedAt)
        }

        override suspend fun deleteById(sessionId: Long) {
            deletedSessionIds += sessionId
        }

        override fun observeDeleted(): Flow<List<SessionEntity>> = MutableStateFlow(emptyList())

        override suspend fun softDeleteById(sessionId: Long, deletedAt: Long) {
            deletedSessionIds += sessionId
        }

        override suspend fun restoreById(sessionId: Long) = Unit

        override suspend fun deleteAllDeleted() = Unit

        override suspend fun purgeDeletedBefore(cutoffTimeMillis: Long) = Unit
    }

    private class FakeCampaignDao : com.example.rpgaudiomixer.data.local.CampaignDao {
        val recordPlayedForSessionRequests = mutableListOf<Pair<Long, Long>>()

        override fun observeAll(): Flow<List<com.example.rpgaudiomixer.data.local.CampaignEntity>> = MutableStateFlow(emptyList())

        override fun observeById(campaignId: Long): Flow<com.example.rpgaudiomixer.data.local.CampaignEntity?> = MutableStateFlow(null)

        override fun observeDeleted(): Flow<List<com.example.rpgaudiomixer.data.local.CampaignEntity>> = MutableStateFlow(emptyList())

        override suspend fun upsert(campaign: com.example.rpgaudiomixer.data.local.CampaignEntity): Long = campaign.id

        override suspend fun softDeleteById(campaignId: Long, deletedAt: Long) = Unit

        override suspend fun restoreById(campaignId: Long) = Unit

        override suspend fun deleteAllDeleted() = Unit

        override suspend fun purgeDeletedBefore(cutoffTimeMillis: Long) = Unit

        override suspend fun deleteById(campaignId: Long) = Unit

        override suspend fun recordPlayedForSession(sessionId: Long, playedAt: Long) {
            recordPlayedForSessionRequests += sessionId to playedAt
        }
    }
}
