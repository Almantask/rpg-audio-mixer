package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SessionSceneRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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

@OptIn(ExperimentalCoroutinesApi::class)
class SessionScenesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val sceneRepository: SceneRepository = mockk()
    private val sessionSceneRepository: SessionSceneRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    private lateinit var viewModel: SessionScenesViewModel

    private val sessionId = "session-1"

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { savedStateHandle.get<String>("sessionId") } returns sessionId
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() {
        // Arrange & Act
        viewModel = SessionScenesViewModel(sceneRepository, sessionSceneRepository, savedStateHandle)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(SessionScenesUiState.Loading::class.java)
    }

    @Test
    fun `when session has linked scenes, state shows linked and available scenes`() = runTest {
        // Arrange
        val scene1 = Scene(id = "1", name = "Tavern", description = null, tags = emptyList())
        val scene2 = Scene(id = "2", name = "Forest", description = null, tags = emptyList())
        val scene3 = Scene(id = "3", name = "Dungeon", description = null, tags = emptyList())

        coEvery { sessionSceneRepository.getScenesBySession(sessionId) } returns listOf("1", "2")
        coEvery { sceneRepository.getSceneById("1") } returns scene1
        coEvery { sceneRepository.getSceneById("2") } returns scene2
        coEvery { sceneRepository.getAllScenes() } returns listOf(scene1, scene2, scene3)

        // Act
        viewModel = SessionScenesViewModel(sceneRepository, sessionSceneRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SessionScenesUiState.Success
        assertThat(state.linkedScenes).hasSize(2)
        assertThat(state.linkedScenes.map { it.id }).containsExactly("1", "2")
        assertThat(state.availableScenes).hasSize(1)
        assertThat(state.availableScenes[0].id).isEqualTo("3")
    }

    @Test
    fun `when session has no linked scenes, all scenes are available`() = runTest {
        // Arrange
        val scene1 = Scene(id = "1", name = "Tavern", description = null, tags = emptyList())
        val scene2 = Scene(id = "2", name = "Forest", description = null, tags = emptyList())

        coEvery { sessionSceneRepository.getScenesBySession(sessionId) } returns emptyList()
        coEvery { sceneRepository.getAllScenes() } returns listOf(scene1, scene2)

        // Act
        viewModel = SessionScenesViewModel(sceneRepository, sessionSceneRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SessionScenesUiState.Success
        assertThat(state.linkedScenes).isEmpty()
        assertThat(state.availableScenes).hasSize(2)
    }

    @Test
    fun `linkScene calls repository and refreshes state`() = runTest {
        // Arrange
        val scene1 = Scene(id = "1", name = "Tavern", description = null, tags = emptyList())
        val scene2 = Scene(id = "2", name = "Forest", description = null, tags = emptyList())

        coEvery { sessionSceneRepository.getScenesBySession(sessionId) } returns emptyList() andThen listOf("1")
        coEvery { sceneRepository.getAllScenes() } returns listOf(scene1, scene2)
        coEvery { sceneRepository.getSceneById("1") } returns scene1
        coEvery { sessionSceneRepository.linkSceneToSession(sessionId, "1") } returns Unit

        viewModel = SessionScenesViewModel(sceneRepository, sessionSceneRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.linkScene("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sessionSceneRepository.linkSceneToSession(sessionId, "1") }
        val state = viewModel.uiState.value as SessionScenesUiState.Success
        assertThat(state.linkedScenes).hasSize(1)
        assertThat(state.linkedScenes[0].id).isEqualTo("1")
        assertThat(state.availableScenes).hasSize(1)
        assertThat(state.availableScenes[0].id).isEqualTo("2")
    }

    @Test
    fun `unlinkScene calls repository and refreshes state`() = runTest {
        // Arrange
        val scene1 = Scene(id = "1", name = "Tavern", description = null, tags = emptyList())
        val scene2 = Scene(id = "2", name = "Forest", description = null, tags = emptyList())

        coEvery { sessionSceneRepository.getScenesBySession(sessionId) } returns listOf("1", "2") andThen listOf("2")
        coEvery { sceneRepository.getSceneById("1") } returns scene1
        coEvery { sceneRepository.getSceneById("2") } returns scene2
        coEvery { sceneRepository.getAllScenes() } returns listOf(scene1, scene2)
        coEvery { sessionSceneRepository.unlinkSceneFromSession(sessionId, "1") } returns Unit

        viewModel = SessionScenesViewModel(sceneRepository, sessionSceneRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.unlinkScene("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sessionSceneRepository.unlinkSceneFromSession(sessionId, "1") }
        val state = viewModel.uiState.value as SessionScenesUiState.Success
        assertThat(state.linkedScenes).hasSize(1)
        assertThat(state.linkedScenes[0].id).isEqualTo("2")
        assertThat(state.availableScenes).hasSize(1)
        assertThat(state.availableScenes[0].id).isEqualTo("1")
    }

    @Test
    fun `when repository throws error, state shows error`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { sessionSceneRepository.getScenesBySession(sessionId) } throws Exception(errorMessage)

        // Act
        viewModel = SessionScenesViewModel(sceneRepository, sessionSceneRepository, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SessionScenesUiState.Error
        assertThat(state.message).isEqualTo(errorMessage)
    }

    @Test
    fun `showImportDialog updates dialog state`() {
        // Arrange
        coEvery { sessionSceneRepository.getScenesBySession(sessionId) } returns emptyList()
        coEvery { sceneRepository.getAllScenes() } returns emptyList()
        viewModel = SessionScenesViewModel(sceneRepository, sessionSceneRepository, savedStateHandle)

        // Act
        viewModel.showImportDialog()

        // Assert
        assertThat(viewModel.showImportDialog.value).isTrue()
    }

    @Test
    fun `hideImportDialog updates dialog state`() {
        // Arrange
        coEvery { sessionSceneRepository.getScenesBySession(sessionId) } returns emptyList()
        coEvery { sceneRepository.getAllScenes() } returns emptyList()
        viewModel = SessionScenesViewModel(sceneRepository, sessionSceneRepository, savedStateHandle)
        viewModel.showImportDialog()

        // Act
        viewModel.hideImportDialog()

        // Assert
        assertThat(viewModel.showImportDialog.value).isFalse()
    }
}
