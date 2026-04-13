package com.example.rpgaudiomixer.app.screens.scenes

import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class ScenesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val sceneFlow = MutableSharedFlow<List<Scene>>(replay = 1)
    private val mockRepository: SceneRepository = mockk {
        every { observeAll() } returns sceneFlow
        coEvery { createScene(any(), any(), any()) } returns 1L
        coEvery { deleteScene(any()) } returns Unit
        coEvery { cloneScene(any()) } returns 99L
    }

    private lateinit var viewModel: ScenesViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScenesViewModel(mockRepository)
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
        assertThat(state).isEqualTo(ScenesUiState.Loading)
    }

    @Test
    fun `uiState emits Success when repository emits scenes`() = runTest(testDispatcher) {
        // Arrange
        val scenes = listOf(
            Scene(id = 1, name = "Battle Theme", description = "Epic fight", tags = "combat,epic"),
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        sceneFlow.emit(scenes)

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(ScenesUiState.Success(scenes))
    }

    @Test
    fun `uiState emits Success with empty list when repository emits empty list`() = runTest(testDispatcher) {
        // Arrange
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        sceneFlow.emit(emptyList())

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(ScenesUiState.Success(emptyList()))
    }

    @Test
    fun `uiState emits Error when repository throws`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "DB failure"
        val errorRepository: SceneRepository = mockk {
            every { observeAll() } returns flow {
                throw IllegalStateException(errorMessage)
            }
        }
        val errorViewModel = ScenesViewModel(errorRepository)
        backgroundScope.launch(UnconfinedTestDispatcher()) { errorViewModel.uiState.collect {} }

        // Act — state is collected above

        // Assert
        assertThat(errorViewModel.uiState.value).isEqualTo(ScenesUiState.Error(errorMessage))
    }

    @Test
    fun `createScene delegates to repository with name only`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp

        // Act
        viewModel.createScene("Tavern Ambience")

        // Assert
        coVerify { mockRepository.createScene("Tavern Ambience", null, null) }
    }

    @Test
    fun `createScene delegates to repository with name and description`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp

        // Act
        viewModel.createScene("Tavern Ambience", "Cozy pub sounds")

        // Assert
        coVerify { mockRepository.createScene("Tavern Ambience", "Cozy pub sounds", null) }
    }

    @Test
    fun `createScene delegates to repository with all parameters`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp

        // Act
        viewModel.createScene("Tavern Ambience", "Cozy pub sounds", "tavern,ambient")

        // Assert
        coVerify { mockRepository.createScene("Tavern Ambience", "Cozy pub sounds", "tavern,ambient") }
    }

    @Test
    fun `deleteScene delegates to repository with soft delete`() = runTest(testDispatcher) {
        // Arrange
        val sceneId = 7L

        // Act
        viewModel.deleteScene(sceneId)

        // Assert
        coVerify { mockRepository.deleteScene(sceneId) }
    }

    @Test
    fun `cloneScene delegates to repository`() = runTest(testDispatcher) {
        // Arrange
        val sceneId = 5L

        // Act
        viewModel.cloneScene(sceneId)

        // Assert
        coVerify { mockRepository.cloneScene(sceneId) }
    }
}
