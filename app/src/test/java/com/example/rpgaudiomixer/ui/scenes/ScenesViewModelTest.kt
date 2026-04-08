package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class ScenesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val sceneRepository: SceneRepository = mockk()

    private lateinit var viewModel: ScenesViewModel

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
        viewModel = ScenesViewModel(sceneRepository)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(ScenesUiState.Loading::class.java)
    }

    @Test
    fun `when scenes exist, state shows scene list`() = runTest {
        // Arrange
        val scenes = listOf(
            Scene(id = "1", name = "Tavern", description = "A cozy tavern", tags = listOf("indoor", "social")),
            Scene(id = "2", name = "Forest", description = "A dark forest", tags = listOf("outdoor", "nature"))
        )
        coEvery { sceneRepository.getAllScenes() } returns scenes

        // Act
        viewModel = ScenesViewModel(sceneRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ScenesUiState.Success
        assertThat(state.scenes).hasSize(2)
        assertThat(state.scenes[0].name).isEqualTo("Tavern")
        assertThat(state.scenes[1].name).isEqualTo("Forest")
    }

    @Test
    fun `when no scenes exist, state shows empty list`() = runTest {
        // Arrange
        coEvery { sceneRepository.getAllScenes() } returns emptyList()

        // Act
        viewModel = ScenesViewModel(sceneRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ScenesUiState.Success
        assertThat(state.scenes).isEmpty()
    }

    @Test
    fun `createScene calls repository and refreshes list`() = runTest {
        // Arrange
        val existingScenes = listOf(
            Scene(id = "1", name = "Existing Scene", description = null, tags = emptyList())
        )
        val newScene = Scene(id = "2", name = "New Scene", description = "A new scene", tags = listOf("tag1"))

        coEvery { sceneRepository.getAllScenes() } returns existingScenes andThen listOf(newScene, existingScenes[0])
        coEvery { sceneRepository.createScene("New Scene", "A new scene", listOf("tag1")) } returns newScene

        viewModel = ScenesViewModel(sceneRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.createScene("New Scene", "A new scene", listOf("tag1"))
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sceneRepository.createScene("New Scene", "A new scene", listOf("tag1")) }
        val state = viewModel.uiState.value as ScenesUiState.Success
        assertThat(state.scenes).hasSize(2)
        assertThat(state.scenes[0].name).isEqualTo("New Scene")
    }

    @Test
    fun `deleteScene calls repository and refreshes list`() = runTest {
        // Arrange
        val scenes = listOf(
            Scene(id = "1", name = "Scene 1", description = null, tags = emptyList()),
            Scene(id = "2", name = "Scene 2", description = null, tags = emptyList())
        )
        coEvery { sceneRepository.getAllScenes() } returns scenes andThen listOf(scenes[1])
        coEvery { sceneRepository.deleteScene("1") } returns Unit

        viewModel = ScenesViewModel(sceneRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteScene("1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sceneRepository.deleteScene("1") }
        val state = viewModel.uiState.value as ScenesUiState.Success
        assertThat(state.scenes).hasSize(1)
        assertThat(state.scenes[0].id).isEqualTo("2")
    }

    @Test
    fun `when repository throws error, state shows error`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { sceneRepository.getAllScenes() } throws Exception(errorMessage)

        // Act
        viewModel = ScenesViewModel(sceneRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ScenesUiState.Error
        assertThat(state.message).isEqualTo(errorMessage)
    }

    @Test
    fun `showCreateDialog updates dialog state`() {
        // Arrange
        coEvery { sceneRepository.getAllScenes() } returns emptyList()
        viewModel = ScenesViewModel(sceneRepository)

        // Act
        viewModel.showCreateDialog()

        // Assert
        assertThat(viewModel.showCreateDialog.value).isTrue()
    }

    @Test
    fun `hideCreateDialog updates dialog state`() {
        // Arrange
        coEvery { sceneRepository.getAllScenes() } returns emptyList()
        viewModel = ScenesViewModel(sceneRepository)
        viewModel.showCreateDialog()

        // Act
        viewModel.hideCreateDialog()

        // Assert
        assertThat(viewModel.showCreateDialog.value).isFalse()
    }
}
