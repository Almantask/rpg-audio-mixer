package com.example.rpgaudiomixer.app.screens.scenes

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
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
class SessionScenesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val linkedScenesFlow = MutableSharedFlow<List<Scene>>(replay = 1)
    private val allScenesFlow = MutableSharedFlow<List<Scene>>(replay = 1)
    private val mockRepository: SceneRepository = mockk {
        every { observeBySession(any()) } returns linkedScenesFlow
        every { observeAll() } returns allScenesFlow
        coEvery { linkToSession(any(), any()) } returns Unit
        coEvery { unlinkFromSession(any(), any()) } returns Unit
    }

    private val savedStateHandle = SavedStateHandle(mapOf("sessionId" to 42L))
    private lateinit var viewModel: SessionScenesViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SessionScenesViewModel(savedStateHandle, mockRepository)
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
        assertThat(state).isEqualTo(SessionScenesUiState.Loading)
    }

    @Test
    fun `uiState emits Success with linked and all scenes`() = runTest(testDispatcher) {
        // Arrange
        val linked = listOf(Scene(id = 1, name = "Tavern"))
        val all = listOf(Scene(id = 1, name = "Tavern"), Scene(id = 2, name = "Forest"))
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        linkedScenesFlow.emit(linked)
        allScenesFlow.emit(all)

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(SessionScenesUiState.Success(linked, all))
    }

    @Test
    fun `linkScene delegates to repository with correct sessionId`() = runTest(testDispatcher) {
        // Act
        viewModel.linkScene(sceneId = 3L)

        // Assert
        coVerify { mockRepository.linkToSession(sceneId = 3L, sessionId = 42L) }
    }

    @Test
    fun `unlinkScene delegates to repository with correct sessionId`() = runTest(testDispatcher) {
        // Act
        viewModel.unlinkScene(sceneId = 5L)

        // Assert
        coVerify { mockRepository.unlinkFromSession(sceneId = 5L, sessionId = 42L) }
    }
}
