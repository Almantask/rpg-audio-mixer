package com.example.rpgaudiomixer.app.screens.home

import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
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
    private val campaignFlow = MutableSharedFlow<List<Campaign>>(replay = 1)
    private val mockRepository: CampaignRepository = mockk {
        every { observeAll() } returns campaignFlow
    }

    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp, no emissions from repo yet

        // Act
        val state = viewModel.uiState.value

        // Assert
        assertThat(state).isEqualTo(HomeUiState.Loading)
    }

    @Test
    fun `uiState emits Success with active campaign when repository emits campaigns`() =
        runTest(testDispatcher) {
            // Arrange
            val campaigns = listOf(
                Campaign(id = 1, name = "Dark Forest", lastPlayedAt = 2000L),
                Campaign(id = 2, name = "Castle Siege", lastPlayedAt = 1000L),
            )
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

            // Act
            campaignFlow.emit(campaigns)

            // Assert
            val state = viewModel.uiState.value
            assertThat(state).isInstanceOf(HomeUiState.Success::class.java)
            val success = state as HomeUiState.Success
            assertThat(success.activeCampaign).isEqualTo(campaigns.first())
            assertThat(success.topAtmosphereTrack).isNull()
            assertThat(success.legendaryAction).isNull()
        }

    @Test
    fun `uiState emits Success with null active campaign when repository emits empty list`() =
        runTest(testDispatcher) {
            // Arrange
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

            // Act
            campaignFlow.emit(emptyList())

            // Assert
            val state = viewModel.uiState.value
            assertThat(state).isInstanceOf(HomeUiState.Success::class.java)
            val success = state as HomeUiState.Success
            assertThat(success.activeCampaign).isNull()
        }

    @Test
    fun `uiState emits Error when repository throws`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "DB failure"
        val errorRepository: CampaignRepository = mockk {
            every { observeAll() } returns flow { throw IllegalStateException(errorMessage) }
        }
        val errorViewModel = HomeViewModel(errorRepository)
        backgroundScope.launch(UnconfinedTestDispatcher()) { errorViewModel.uiState.collect {} }

        // Assert
        val state = errorViewModel.uiState.value
        assertThat(state).isInstanceOf(HomeUiState.Error::class.java)
        assertThat((state as HomeUiState.Error).message).isEqualTo(errorMessage)
    }
}
