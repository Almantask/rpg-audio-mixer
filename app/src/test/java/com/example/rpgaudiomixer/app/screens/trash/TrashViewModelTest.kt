package com.example.rpgaudiomixer.app.screens.trash

import com.example.rpgaudiomixer.app.domain.model.Campaign
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.model.Session
import com.example.rpgaudiomixer.app.domain.model.TrashItem
import com.example.rpgaudiomixer.app.domain.model.TrashItemType
import com.example.rpgaudiomixer.app.domain.repository.CampaignRepository
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import com.example.rpgaudiomixer.app.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrashViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val campaignFlow = MutableSharedFlow<List<Campaign>>(replay = 1)
    private val sessionFlow = MutableSharedFlow<List<Session>>(replay = 1)
    private val sceneFlow = MutableSharedFlow<List<Scene>>(replay = 1)

    private val mockCampaignRepository: CampaignRepository = mockk {
        every { observeDeleted() } returns campaignFlow
        coEvery { restore(any()) } just Runs
        coEvery { hardDelete(any()) } just Runs
        coEvery { purgeAllDeleted() } just Runs
    }
    private val mockSessionRepository: SessionRepository = mockk {
        every { observeDeleted() } returns sessionFlow
        coEvery { restore(any()) } just Runs
        coEvery { hardDelete(any()) } just Runs
        coEvery { purgeAllDeleted() } just Runs
    }
    private val mockSceneRepository: SceneRepository = mockk {
        every { observeDeleted() } returns sceneFlow
        coEvery { restore(any()) } just Runs
        coEvery { hardDelete(any()) } just Runs
        coEvery { purgeAllDeleted() } just Runs
    }

    private lateinit var viewModel: TrashViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TrashViewModel(mockCampaignRepository, mockSessionRepository, mockSceneRepository)
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
        assertThat(state).isEqualTo(TrashUiState.Loading)
    }

    @Test
    fun `uiState emits Success with all deleted items sorted by deletedAt desc`() = runTest(testDispatcher) {
        // Arrange
        val campaign = Campaign(id = 1, name = "Old Campaign", deletedAt = 1000L)
        val session = Session(id = 2, campaignId = 0, name = "Old Session", deletedAt = 3000L)
        val scene = Scene(id = 3, name = "Old Scene", deletedAt = 2000L)
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        campaignFlow.emit(listOf(campaign))
        sessionFlow.emit(listOf(session))
        sceneFlow.emit(listOf(scene))

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(TrashUiState.Success::class.java)
        val success = state as TrashUiState.Success
        assertThat(success.items).hasSize(3)
        // Sorted by deletedAt DESC: session(3000) > scene(2000) > campaign(1000)
        assertThat(success.items[0].name).isEqualTo("Old Session")
        assertThat(success.items[1].name).isEqualTo("Old Scene")
        assertThat(success.items[2].name).isEqualTo("Old Campaign")
    }

    @Test
    fun `uiState emits Success with empty list when nothing deleted`() = runTest(testDispatcher) {
        // Arrange
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        campaignFlow.emit(emptyList())
        sessionFlow.emit(emptyList())
        sceneFlow.emit(emptyList())

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(TrashUiState.Success::class.java)
        val success = state as TrashUiState.Success
        assertThat(success.items).isEmpty()
    }

    @Test
    fun `restore calls campaignRepository restore for campaign item`() = runTest(testDispatcher) {
        // Arrange
        val item = TrashItem(id = 1L, name = "Dragon Keep", type = TrashItemType.CAMPAIGN, deletedAt = 1000L)

        // Act
        viewModel.restore(item)
        advanceUntilIdle()

        // Assert
        coVerify { mockCampaignRepository.restore(match { it.id == 1L && it.name == "Dragon Keep" }) }
    }

    @Test
    fun `restore calls sessionRepository restore for session item`() = runTest(testDispatcher) {
        // Arrange
        val item = TrashItem(id = 2L, name = "Boss Fight", type = TrashItemType.SESSION, deletedAt = 2000L)

        // Act
        viewModel.restore(item)
        advanceUntilIdle()

        // Assert
        coVerify { mockSessionRepository.restore(match { it.id == 2L && it.name == "Boss Fight" }) }
    }

    @Test
    fun `restore calls sceneRepository restore for scene item`() = runTest(testDispatcher) {
        // Arrange
        val item = TrashItem(id = 3L, name = "Tavern", type = TrashItemType.SCENE, deletedAt = 3000L)

        // Act
        viewModel.restore(item)
        advanceUntilIdle()

        // Assert
        coVerify { mockSceneRepository.restore(match { it.id == 3L && it.name == "Tavern" }) }
    }

    @Test
    fun `hardDelete calls campaignRepository hardDelete for campaign item`() = runTest(testDispatcher) {
        // Arrange
        val item = TrashItem(id = 1L, name = "Dragon Keep", type = TrashItemType.CAMPAIGN, deletedAt = 1000L)

        // Act
        viewModel.hardDelete(item)
        advanceUntilIdle()

        // Assert
        coVerify { mockCampaignRepository.hardDelete(match { it.id == 1L && it.name == "Dragon Keep" }) }
    }

    @Test
    fun `emptyVault calls purgeAllDeleted on all repositories`() = runTest(testDispatcher) {
        // Act
        viewModel.emptyVault()
        advanceUntilIdle()

        // Assert
        coVerify { mockCampaignRepository.purgeAllDeleted() }
        coVerify { mockSessionRepository.purgeAllDeleted() }
        coVerify { mockSceneRepository.purgeAllDeleted() }
    }
}
