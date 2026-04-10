package com.example.rpgaudiomixer.app.ui.home

import com.example.rpgaudiomixer.domain.campaign.Campaign
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.library.SoundscapeTrack
import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.scene.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val campaignRepository = mockk<CampaignRepository>()
    private val sceneRepository = mockk<SceneRepository>()
    private val soundscapeRepository = mockk<SoundscapeRepository>()
    private val fxRepository = mockk<FxRepository>()

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mocks to avoid crashes on init
        every { campaignRepository.observeMostRecent() } returns flowOf(null)
        every { soundscapeRepository.observeMostPlayedTrack() } returns flowOf(null)
        every { fxRepository.observeMostPlayedTrack() } returns flowOf(null)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads active campaign and stats`() = runTest {
        val campaign = Campaign(id = 1, name = "Test Campaign", lastOpenedSceneId = 10)
        val scene = Scene(id = 10, name = "Test Scene")
        val track = SoundscapeTrack(
            id = 1, 
            categoryId = 1, 
            name = "Ambience", 
            filePath = "path/to/ambience", 
            intensityLevel = IntensityLevel.I
        )
        val fx = FxTrack(id = 1, name = "Clash", filePath = "path/to/clash", tags = emptyList(), durationMs = 1000)

        every { campaignRepository.observeMostRecent() } returns flowOf(campaign)
        every { sceneRepository.observeById(10) } returns flowOf(scene)
        every { soundscapeRepository.observeMostPlayedTrack() } returns flowOf(track)
        every { fxRepository.observeMostPlayedTrack() } returns flowOf(fx)

        val viewModel = HomeViewModel(
            campaignRepository, sceneRepository, soundscapeRepository, fxRepository
        )

        val state = viewModel.uiState.filter { !it.isLoading }.first()
        assertThat(state.activeCampaign).isEqualTo(campaign)
        assertThat(state.resumeScene).isEqualTo(scene)
        assertThat(state.topAtmosphere).isEqualTo(track)
        assertThat(state.legendaryAction).isEqualTo(fx)
    }
}
