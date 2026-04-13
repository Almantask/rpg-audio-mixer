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
        coEvery { createScene(any()) } returns Unit
        coEvery { deleteScene(any()) } returns Unit
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
        // Arrange — viewModel created in setUp, no emissions yet

        // Act
        val state = viewModel.uiState.value

        // Assert
        assertThat(state).isEqualTo(ScenesUiState.Loading)
    }

    @Test
    fun `uiState emits Success when repository emits scenes`() = runTest(testDispatcher) {
        // Arrange
        val scenes = listOf(Scene(id = 1, name = "Tavern"))
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        sceneFlow.emit(scenes)

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(ScenesUiState.Success(scenes))
    }

    @Test
    fun `uiState emits Success with empty list`() = runTest(testDispatcher) {
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
        val errorRepo: SceneRepository = mockk {
            every { observeAll() } returns flow { throw IllegalStateException(errorMessage) }
        }
        val errorViewModel = ScenesViewModel(errorRepo)
        backgroundScope.launch(UnconfinedTestDispatcher()) { errorViewModel.uiState.collect {} }

        // Assert
        assertThat(errorViewModel.uiState.value).isEqualTo(ScenesUiState.Error(errorMessage))
    }

    @Test
    fun `createScene delegates to repository`() = runTest(testDispatcher) {
        // Act
        viewModel.createScene("Dark Forest")

        // Assert
        coVerify { mockRepository.createScene("Dark Forest") }
    }

    @Test
    fun `deleteScene delegates to repository`() = runTest(testDispatcher) {
        // Arrange
        val scene = Scene(id = 5, name = "Dragon Lair")

        // Act
        viewModel.deleteScene(scene)

        // Assert
        coVerify { mockRepository.deleteScene(scene) }
    }
}
