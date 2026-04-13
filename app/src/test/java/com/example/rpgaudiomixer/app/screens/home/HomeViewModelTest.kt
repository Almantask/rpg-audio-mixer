package com.example.rpgaudiomixer.app.screens.home

import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val campaignFlow = MutableSharedFlow<Campaign?>(replay = 1)
    private val sceneFlow = MutableSharedFlow<Scene?>(replay = 1)

    private val mockCampaignRepository: CampaignRepository = mockk {
        every { observeLatest() } returns campaignFlow
    }
    private val mockSceneRepository: SceneRepository = mockk {
        every { observeLatest() } returns sceneFlow
    }

    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(mockCampaignRepository, mockSceneRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp, no emissions yet

        // Act
        val state = viewModel.uiState.value

        // Assert
        assertThat(state).isEqualTo(HomeUiState.Loading)
    }

    @Test
    fun `uiState emits Success with latest campaign and scene`() = runTest(testDispatcher) {
        // Arrange
        val campaign = Campaign(id = 1, name = "Dark Forest")
        val scene = Scene(id = 10, name = "Tavern")
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        campaignFlow.emit(campaign)
        sceneFlow.emit(scene)

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(HomeUiState.Success::class.java)
        val success = state as HomeUiState.Success
        assertThat(success.latestCampaign).isEqualTo(campaign)
        assertThat(success.latestScene).isEqualTo(scene)
    }

    @Test
    fun `uiState emits Success when no campaign exists (null)`() = runTest(testDispatcher) {
        // Arrange
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        campaignFlow.emit(null)
        sceneFlow.emit(null)

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(HomeUiState.Success::class.java)
        val success = state as HomeUiState.Success
        assertThat(success.latestCampaign).isNull()
        assertThat(success.latestScene).isNull()
    }

    @Test
    fun `uiState emits Success when campaign exists but no scene (null)`() = runTest(testDispatcher) {
        // Arrange
        val campaign = Campaign(id = 2, name = "Dragon Keep")
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        campaignFlow.emit(campaign)
        sceneFlow.emit(null)

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(HomeUiState.Success::class.java)
        val success = state as HomeUiState.Success
        assertThat(success.latestCampaign).isEqualTo(campaign)
        assertThat(success.latestScene).isNull()
    }

    @Test
    fun `uiState emits Error when repository throws`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "DB failure"
        val errorCampaignRepository: CampaignRepository = mockk {
            every { observeLatest() } returns flow { throw IllegalStateException(errorMessage) }
        }
        val errorSceneRepository: SceneRepository = mockk {
            every { observeLatest() } returns sceneFlow
        }
        val errorViewModel = HomeViewModel(errorCampaignRepository, errorSceneRepository)
        backgroundScope.launch(UnconfinedTestDispatcher()) { errorViewModel.uiState.collect {} }

        // Assert
        assertThat(errorViewModel.uiState.value).isEqualTo(HomeUiState.Error(errorMessage))
    }
}
