package com.example.rpgaudiomixer.ui.home

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.MostPlayedSoundscapeTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun activeCampaign_is_the_most_recently_played_campaign() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val campaignRepository = FakeCampaignRepository(
            campaigns = listOf(
                Campaign(id = 1L, name = "Old Campaign", coverArtUri = null, lastPlayedAt = 10L),
                Campaign(id = 2L, name = "Curse of Strahd", coverArtUri = null, lastPlayedAt = 20L),
            ),
        )

        // Act
        val viewModel = HomeViewModel(
            campaignRepository = campaignRepository,
            sessionRepository = FakeSessionRepository(),
            soundscapeRepository = FakeSoundscapeRepository(),
            fxRepository = FakeFxRepository(),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.activeCampaign?.name).isEqualTo("Curse of Strahd")
    }

    @Test
    fun resumeJourney_uses_the_last_opened_scene_for_the_active_campaign() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val campaignRepository = FakeCampaignRepository(
            campaigns = listOf(
                Campaign(id = 2L, name = "Curse of Strahd", coverArtUri = null, lastPlayedAt = 20L),
            ),
        )

        // Act
        val viewModel = HomeViewModel(
            campaignRepository = campaignRepository,
            sessionRepository = FakeSessionRepository(
                lastOpenedScene = Scene(
                    id = 8L,
                    name = "The Foyer",
                    description = null,
                    tags = listOf("haunted"),
                    masterVolume = 0.7f,
                ),
            ),
            soundscapeRepository = FakeSoundscapeRepository(),
            fxRepository = FakeFxRepository(),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.resumeScene?.name).isEqualTo("The Foyer")
    }

    @Test
    fun topAtmosphere_uses_the_most_played_loopable_track() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val viewModel = HomeViewModel(
            campaignRepository = FakeCampaignRepository(),
            sessionRepository = FakeSessionRepository(),
            soundscapeRepository = FakeSoundscapeRepository(
                mostPlayedTrack = MostPlayedSoundscapeTrack(
                    id = 3L,
                    categoryId = 2L,
                    categoryName = "Interior",
                    name = "Tavern Warmth",
                    filePath = "content://tavern",
                    intensityLevel = IntensityLevel.II,
                    mixVolumePercent = 70,
                    displayOrder = 0,
                    playCount = 18,
                ),
            ),
            fxRepository = FakeFxRepository(),
        )

        // Act
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.topAtmosphere?.name).isEqualTo("Tavern Warmth")
        assertThat(viewModel.uiState.value.topAtmosphere?.categoryName).isEqualTo("Interior")
    }

    @Test
    fun legendaryAction_uses_the_most_played_fx_track() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val viewModel = HomeViewModel(
            campaignRepository = FakeCampaignRepository(),
            sessionRepository = FakeSessionRepository(),
            soundscapeRepository = FakeSoundscapeRepository(),
            fxRepository = FakeFxRepository(
                mostPlayedTrack = FxTrack(
                    id = 6L,
                    name = "Thunder Crack",
                    filePath = "content://thunder",
                    tags = listOf("Magic", "Storm"),
                    durationMs = 1_000L,
                    playCount = 20,
                    isDemoContent = false,
                ),
            ),
        )

        // Act
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.legendaryAction?.name).isEqualTo("Thunder Crack")
    }

    @Test
    fun empty_repository_exposes_an_empty_home_state() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val viewModel = HomeViewModel(
            campaignRepository = FakeCampaignRepository(),
            sessionRepository = FakeSessionRepository(),
            soundscapeRepository = FakeSoundscapeRepository(),
            fxRepository = FakeFxRepository(),
        )

        // Act
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.activeCampaign).isNull()
        assertThat(viewModel.uiState.value.resumeScene).isNull()
    }

    private class FakeCampaignRepository(
        campaigns: List<Campaign> = emptyList(),
    ) : CampaignRepository {
        private val campaignFlow = MutableStateFlow(campaigns.sortedByDescending { it.lastPlayedAt })

        override fun observeCampaigns(): Flow<List<Campaign>> = campaignFlow

        override fun observeCampaign(campaignId: Long): Flow<Campaign?> = campaignFlow.map { campaigns ->
            campaigns.firstOrNull { it.id == campaignId }
        }

        override fun observeActiveCampaign(): Flow<Campaign?> = campaignFlow.map { it.firstOrNull() }

        override suspend fun createCampaign(name: String, coverArtUri: String?): Long {
            error("Not needed in this test")
        }

        override suspend fun deleteCampaign(campaignId: Long, deletedAtMillis: Long) {
            error("Not needed in this test")
        }

        override suspend fun markCampaignPlayed(campaignId: Long, playedAtMillis: Long) {
            error("Not needed in this test")
        }
    }

    private class FakeSessionRepository(
        lastOpenedScene: Scene? = null,
    ) : SessionRepository {
        private val lastOpenedSceneFlow = MutableStateFlow(lastOpenedScene)

        override fun observeSessionsByCampaign(campaignId: Long): Flow<List<Session>> = MutableStateFlow(emptyList())

        override fun observeSession(sessionId: Long): Flow<Session?> = MutableStateFlow(null)

        override fun observeScenesBySession(sessionId: Long): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeLastOpenedSceneInCampaign(campaignId: Long): Flow<Scene?> = lastOpenedSceneFlow

        override suspend fun createSession(campaignId: Long, name: String, dateMillis: Long, coverArtUri: String?): Long {
            error("Not needed in this test")
        }

        override suspend fun deleteSession(sessionId: Long, deletedAtMillis: Long) {
            error("Not needed in this test")
        }

        override suspend fun linkScenes(sessionId: Long, sceneIds: List<Long>) {
            error("Not needed in this test")
        }

        override suspend fun unlinkScene(sessionId: Long, sceneId: Long) {
            error("Not needed in this test")
        }

        override suspend fun markSceneOpened(sessionId: Long, sceneId: Long, openedAtMillis: Long) {
            error("Not needed in this test")
        }
    }

    private class FakeSoundscapeRepository(
        mostPlayedTrack: MostPlayedSoundscapeTrack? = null,
    ) : SoundscapeRepository {
        private val mostPlayedTrackFlow = MutableStateFlow(mostPlayedTrack)

        override fun observeCategories() = MutableStateFlow(emptyList<com.example.rpgaudiomixer.domain.model.SoundscapeCategory>())

        override fun observeCategory(categoryId: Long) = MutableStateFlow<com.example.rpgaudiomixer.domain.model.SoundscapeCategory?>(null)

        override fun observeTracks(categoryId: Long) = MutableStateFlow(emptyList<com.example.rpgaudiomixer.domain.model.SoundscapeTrack>())

        override fun observeMostPlayedTrack(): Flow<MostPlayedSoundscapeTrack?> = mostPlayedTrackFlow

        override suspend fun createCategory(name: String): Long {
            error("Not needed in this test")
        }

        override suspend fun deleteCategory(categoryId: Long, deletedAtMillis: Long) {
            error("Not needed in this test")
        }

        override suspend fun saveTracks(categoryId: Long, tracks: List<com.example.rpgaudiomixer.domain.model.SoundscapeTrack>) {
            error("Not needed in this test")
        }

        override suspend fun installDemoSoundscapes() {
            error("Not needed in this test")
        }
    }

    private class FakeFxRepository(
        mostPlayedTrack: FxTrack? = null,
    ) : FxRepository {
        private val mostPlayedTrackFlow = MutableStateFlow(mostPlayedTrack)

        override fun observeTracks(): Flow<List<FxTrack>> = MutableStateFlow(emptyList())

        override fun observeMostPlayedTrack(): Flow<FxTrack?> = mostPlayedTrackFlow

        override suspend fun importTrack(name: String, filePath: String): Result<Long> {
            error("Not needed in this test")
        }

        override suspend fun installDemoTracks() {
            error("Not needed in this test")
        }

        override suspend fun updateTrack(track: FxTrack) {
            error("Not needed in this test")
        }

        override suspend fun deleteTrack(trackId: Long, deletedAtMillis: Long) {
            error("Not needed in this test")
        }
    }
}
