package com.example.rpgaudiomixer.ui.home

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.TrackStats
import com.example.rpgaudiomixer.domain.model.TrackType
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.TrackStatsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val campaignRepository: CampaignRepository = mockk()
    private val sceneRepository: SceneRepository = mockk()
    private val trackStatsRepository: TrackStatsRepository = mockk()

    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        // Arrange & Act
        viewModel = HomeViewModel(campaignRepository, sceneRepository, trackStatsRepository)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(HomeUiState.Loading::class.java)
    }

    @Test
    fun `when campaign exists, state shows campaign with scene and stats`() = runTest {
        // Arrange
        val campaign = Campaign(
            id = "campaign-1",
            name = "Curse of Strahd",
            lastPlayedAt = Instant.now()
        )
        val scene = Scene(
            id = "scene-1",
            name = "The Foyer",
            campaignId = "campaign-1",
            lastOpenedAt = Instant.now()
        )
        val loopableTrack = TrackStats(
            trackId = "track-1",
            name = "Tavern Warmth",
            type = TrackType.LOOPABLE,
            playCount = 42
        )
        val fxTrack = TrackStats(
            trackId = "track-2",
            name = "Thunder Crack",
            type = TrackType.FX,
            playCount = 15
        )

        coEvery { campaignRepository.getMostRecentlyPlayedCampaign() } returns campaign
        coEvery { sceneRepository.getLastOpenedSceneInCampaign("campaign-1") } returns scene
        coEvery { trackStatsRepository.getMostPlayedTrack(TrackType.LOOPABLE) } returns loopableTrack
        coEvery { trackStatsRepository.getMostPlayedTrack(TrackType.FX) } returns fxTrack

        // Act
        viewModel = HomeViewModel(campaignRepository, sceneRepository, trackStatsRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as HomeUiState.Success
        assertThat(state.activeCampaign).isEqualTo(campaign)
        assertThat(state.lastScene).isEqualTo(scene)
        assertThat(state.topAtmosphere).isEqualTo(loopableTrack)
        assertThat(state.legendaryAction).isEqualTo(fxTrack)
    }

    @Test
    fun `when no campaign exists, state shows empty state`() = runTest {
        // Arrange
        coEvery { campaignRepository.getMostRecentlyPlayedCampaign() } returns null
        coEvery { sceneRepository.getLastOpenedSceneInCampaign(any()) } returns null
        coEvery { trackStatsRepository.getMostPlayedTrack(TrackType.LOOPABLE) } returns null
        coEvery { trackStatsRepository.getMostPlayedTrack(TrackType.FX) } returns null

        // Act
        viewModel = HomeViewModel(campaignRepository, sceneRepository, trackStatsRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as HomeUiState.Success
        assertThat(state.activeCampaign).isNull()
        assertThat(state.lastScene).isNull()
        assertThat(state.topAtmosphere).isNull()
        assertThat(state.legendaryAction).isNull()
    }

    @Test
    fun `when repository throws error, state shows error`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { campaignRepository.getMostRecentlyPlayedCampaign() } throws Exception(errorMessage)

        // Act
        viewModel = HomeViewModel(campaignRepository, sceneRepository, trackStatsRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as HomeUiState.Error
        assertThat(state.message).isEqualTo(errorMessage)
    }
}
