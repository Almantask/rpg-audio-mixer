package com.example.rpgaudiomixer.app.screens.sessionscenes

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
class SessionScenesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val sessionId = 42L
    private val sessionScenesFlow = MutableSharedFlow<List<Scene>>(replay = 1)
    private val allScenesFlow = MutableSharedFlow<List<Scene>>(replay = 1)
    private val mockRepository: SceneRepository = mockk {
        every { observeScenesForSession(sessionId) } returns sessionScenesFlow
        every { observeAll() } returns allScenesFlow
        coEvery { unlinkSceneFromSession(any(), any()) } returns Unit
        coEvery { linkSceneToSession(any(), any()) } returns Unit
    }

    private val savedStateHandle = SavedStateHandle(mapOf("sessionId" to sessionId))

    private lateinit var viewModel: SessionScenesViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SessionScenesViewModel(mockRepository, savedStateHandle)
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
        assertThat(state).isEqualTo(SessionScenesUiState.Loading)
    }

    @Test
    fun `uiState emits Success when repository emits session scenes`() = runTest(testDispatcher) {
        // Arrange
        val scenes = listOf(
            Scene(id = 1, name = "Forest Ambience", tags = "nature"),
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        sessionScenesFlow.emit(scenes)

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(SessionScenesUiState.Success(scenes))
    }

    @Test
    fun `uiState emits Success with empty list when repository emits empty list`() = runTest(testDispatcher) {
        // Arrange
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        sessionScenesFlow.emit(emptyList())

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(SessionScenesUiState.Success(emptyList()))
    }

    @Test
    fun `uiState emits Error when repository throws`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "DB failure"
        val errorRepository: SceneRepository = mockk {
            every { observeScenesForSession(sessionId) } returns flow {
                throw IllegalStateException(errorMessage)
            }
            every { observeAll() } returns allScenesFlow
        }
        val errorViewModel = SessionScenesViewModel(errorRepository, savedStateHandle)
        backgroundScope.launch(UnconfinedTestDispatcher()) { errorViewModel.uiState.collect {} }

        // Act — state is collected above

        // Assert
        assertThat(errorViewModel.uiState.value).isEqualTo(SessionScenesUiState.Error(errorMessage))
    }

    @Test
    fun `unlinkScene delegates to repository`() = runTest(testDispatcher) {
        // Arrange
        val sceneId = 3L

        // Act
        viewModel.unlinkScene(sceneId)

        // Assert
        coVerify { mockRepository.unlinkSceneFromSession(sessionId, sceneId) }
    }

    @Test
    fun `linkScenes delegates to repository for each scene`() = runTest(testDispatcher) {
        // Arrange
        val sceneIds = listOf(1L, 2L, 3L)

        // Act
        viewModel.linkScenes(sceneIds)

        // Assert
        coVerify { mockRepository.linkSceneToSession(sessionId, 1L) }
        coVerify { mockRepository.linkSceneToSession(sessionId, 2L) }
        coVerify { mockRepository.linkSceneToSession(sessionId, 3L) }
    }

    @Test
    fun `allScenes flow exposes all scenes from repository`() = runTest(testDispatcher) {
        // Arrange
        val scenes = listOf(
            Scene(id = 1, name = "Battle"),
            Scene(id = 2, name = "Tavern"),
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.allScenes.collect {} }

        // Act
        allScenesFlow.emit(scenes)

        // Assert
        assertThat(viewModel.allScenes.value).isEqualTo(scenes)
    }

    @Test
    fun `linkScenes with empty list does not call repository`() = runTest(testDispatcher) {
        // Arrange — empty list

        // Act
        viewModel.linkScenes(emptyList())

        // Assert
        coVerify(exactly = 0) { mockRepository.linkSceneToSession(any(), any()) }
    }
}
