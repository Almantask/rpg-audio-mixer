package com.example.rpgaudiomixer.ui.soundscapelibrary

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SoundscapeLibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val soundscapeRepository: SoundscapeRepository = mockk()

    private lateinit var viewModel: SoundscapeLibraryViewModel

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
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(SoundscapeLibraryUiState.Loading::class.java)
    }

    @Test
    fun `when categories exist, state shows category list`() = runTest {
        // Arrange
        val categories = listOf(
            SoundscapeCategory(
                id = "1",
                name = "Forest",
                trackCountByLevel = mapOf(
                    IntensityLevel.I to 3,
                    IntensityLevel.II to 2,
                    IntensityLevel.III to 0
                )
            ),
            SoundscapeCategory(
                id = "2",
                name = "Combat",
                trackCountByLevel = mapOf(
                    IntensityLevel.I to 0,
                    IntensityLevel.II to 3,
                    IntensityLevel.III to 3
                )
            )
        )
        coEvery { soundscapeRepository.observeAllCategories() } returns flowOf(categories)

        // Act
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SoundscapeLibraryUiState.Success
        assertThat(state.categories).hasSize(2)
        assertThat(state.categories[0].name).isEqualTo("Forest")
        assertThat(state.categories[1].name).isEqualTo("Combat")
    }

    @Test
    fun `when no categories exist, state shows empty list`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())

        // Act
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SoundscapeLibraryUiState.Success
        assertThat(state.categories).isEmpty()
    }

    @Test
    fun `createCategory creates a new category`() = runTest {
        // Arrange
        val newCategory = SoundscapeCategory(id = "new", name = "Mystery")
        coEvery { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())
        coEvery { soundscapeRepository.createCategory("Mystery", null, null) } returns newCategory
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        advanceUntilIdle()

        // Act
        viewModel.createCategory("Mystery")
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { soundscapeRepository.createCategory("Mystery", null, null) }
    }

    @Test
    fun `deleteCategory deletes the category`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())
        coEvery { soundscapeRepository.deleteCategory("1") } returns Unit
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        advanceUntilIdle()

        // Act
        viewModel.deleteCategory("1")
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { soundscapeRepository.deleteCategory("1") }
    }

    @Test
    fun `showCreateDialog updates dialog state`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)

        // Act
        viewModel.showCreateDialog()

        // Assert
        assertThat(viewModel.showCreateDialog.value).isTrue()
    }

    @Test
    fun `hideCreateDialog updates dialog state`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        viewModel.showCreateDialog()

        // Act
        viewModel.hideCreateDialog()

        // Assert
        assertThat(viewModel.showCreateDialog.value).isFalse()
    }

    @Test
    fun `on error loading categories, state shows error`() = runTest {
        // Arrange
        coEvery { soundscapeRepository.observeAllCategories() } throws Exception("Failed to load")

        // Act
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as SoundscapeLibraryUiState.Error
        assertThat(state.message).contains("Failed to load")
    }
}
