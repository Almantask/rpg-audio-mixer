package com.example.rpgaudiomixer.app.screens.activescene

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.app.domain.repository.SoundscapeCategoryRepository
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
class ActiveSceneViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val categoriesFlow = MutableSharedFlow<List<SoundscapeCategory>>(replay = 1)
    private val mockCategoryRepository: SoundscapeCategoryRepository = mockk {
        every { observeByScene(any()) } returns categoriesFlow
    }
    private val mockMusicPlayer: MixedMusicPlayer = mockk(relaxed = true)

    private val savedStateHandle = SavedStateHandle(mapOf("sceneId" to 1L))
    private lateinit var viewModel: ActiveSceneViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ActiveSceneViewModel(savedStateHandle, mockCategoryRepository, mockMusicPlayer)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState_emitsLoading_initially`() = runTest(testDispatcher) {
        // Arrange — viewModel created in setUp, no emissions yet

        // Act
        val state = viewModel.uiState.value

        // Assert
        assertThat(state).isEqualTo(ActiveSceneUiState.Loading)
    }

    @Test
    fun `uiState_emitsSuccess_withCategories`() = runTest(testDispatcher) {
        // Arrange
        val categories = listOf(
            SoundscapeCategory(id = 1L, sceneId = 1L, name = "Forest"),
            SoundscapeCategory(id = 2L, sceneId = 1L, name = "Rain"),
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }

        // Act
        categoriesFlow.emit(categories)

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(ActiveSceneUiState.Success::class.java)
        assertThat((state as ActiveSceneUiState.Success).categories).isEqualTo(categories)
    }

    @Test
    fun `toggleCategory_whenNotPlaying_startsLoopingAndAddsToPlayingSet`() = runTest(testDispatcher) {
        // Arrange
        val category = SoundscapeCategory(id = 1L, sceneId = 1L, name = "Forest")
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }
        categoriesFlow.emit(listOf(category))

        // Act
        viewModel.toggleCategory(category)

        // Assert
        verify { mockMusicPlayer.playLoopingSound("Forest") }
        val state = viewModel.uiState.value as ActiveSceneUiState.Success
        assertThat(state.playingCategories).contains("Forest")
    }

    @Test
    fun `toggleCategory_whenPlaying_pausesAndRemovesFromPlayingSet`() = runTest(testDispatcher) {
        // Arrange
        val category = SoundscapeCategory(id = 1L, sceneId = 1L, name = "Forest")
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }
        categoriesFlow.emit(listOf(category))
        viewModel.toggleCategory(category) // start playing

        // Act
        viewModel.toggleCategory(category) // pause

        // Assert
        verify { mockMusicPlayer.pauseLoopingSound("Forest") }
        val state = viewModel.uiState.value as ActiveSceneUiState.Success
        assertThat(state.playingCategories).doesNotContain("Forest")
    }

    @Test
    fun `toggleCategory_twoCategoriesPlaySimultaneously`() = runTest(testDispatcher) {
        // Arrange
        val forest = SoundscapeCategory(id = 1L, sceneId = 1L, name = "Forest")
        val rain = SoundscapeCategory(id = 2L, sceneId = 1L, name = "Rain")
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect {} }
        categoriesFlow.emit(listOf(forest, rain))

        // Act
        viewModel.toggleCategory(forest)
        viewModel.toggleCategory(rain)

        // Assert
        val state = viewModel.uiState.value as ActiveSceneUiState.Success
        assertThat(state.playingCategories).containsExactlyInAnyOrder("Forest", "Rain")
    }
}
