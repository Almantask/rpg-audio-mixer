package com.example.rpgaudiomixer.ui.soundscapes

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private val soundscapeRepository: SoundscapeRepository = mockk(relaxed = true)
    private lateinit var viewModel: SoundscapeLibraryViewModel

    private val sampleCategory1 = SoundscapeCategory(
        id = 1L,
        name = "Forest",
        iconResId = null,
        themeLabel = null
    )

    private val sampleCategory2 = SoundscapeCategory(
        id = 2L,
        name = "Tavern",
        iconResId = null,
        themeLabel = null
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange
        every { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())

        // Act
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(SoundscapeLibraryUiState.Loading::class.java)
    }

    @Test
    fun `loadCategories emits Success state with categories and track counts`() = runTest {
        // Arrange
        val categories = listOf(sampleCategory1, sampleCategory2)
        every { soundscapeRepository.observeAllCategories() } returns flowOf(categories)
        coEvery { soundscapeRepository.getTrackCountByIntensity(1L, IntensityLevel.I) } returns 3
        coEvery { soundscapeRepository.getTrackCountByIntensity(1L, IntensityLevel.II) } returns 5
        coEvery { soundscapeRepository.getTrackCountByIntensity(1L, IntensityLevel.III) } returns 2
        coEvery { soundscapeRepository.getTrackCountByIntensity(2L, IntensityLevel.I) } returns 1
        coEvery { soundscapeRepository.getTrackCountByIntensity(2L, IntensityLevel.II) } returns 2
        coEvery { soundscapeRepository.getTrackCountByIntensity(2L, IntensityLevel.III) } returns 1

        // Act
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeLibraryUiState.Success::class.java)
        val successState = state as SoundscapeLibraryUiState.Success
        assertThat(successState.categories).hasSize(2)
        assertThat(successState.categories[0].category).isEqualTo(sampleCategory1)
        assertThat(successState.categories[0].levelICounts).isEqualTo(3)
        assertThat(successState.categories[0].levelIICounts).isEqualTo(5)
        assertThat(successState.categories[0].levelIIICounts).isEqualTo(2)
        assertThat(successState.categories[1].category).isEqualTo(sampleCategory2)
    }

    @Test
    fun `loadCategories emits Success state with empty list when no categories`() = runTest {
        // Arrange
        every { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())

        // Act
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeLibraryUiState.Success::class.java)
        assertThat((state as SoundscapeLibraryUiState.Success).categories).isEmpty()
    }

    @Test
    fun `loadCategories emits Error state when repository throws exception`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        every { soundscapeRepository.observeAllCategories() } returns flowOf()
        coEvery { soundscapeRepository.observeAllCategories() } throws RuntimeException(errorMessage)

        // Act
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeLibraryUiState.Error::class.java)
        assertThat((state as SoundscapeLibraryUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `createCategory calls repository create`() = runTest {
        // Arrange
        every { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { soundscapeRepository.createCategory("Dungeon") } returns 1L

        // Act
        viewModel.createCategory("Dungeon")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { soundscapeRepository.createCategory("Dungeon") }
    }

    @Test
    fun `createCategory emits Error state when repository throws exception`() = runTest {
        // Arrange
        every { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to create"
        coEvery { soundscapeRepository.createCategory(any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.createCategory("Dungeon")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeLibraryUiState.Error::class.java)
        assertThat((state as SoundscapeLibraryUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `deleteCategory calls repository delete`() = runTest {
        // Arrange
        every { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.deleteCategory(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { soundscapeRepository.deleteCategory(1L) }
    }

    @Test
    fun `deleteCategory emits Error state when repository throws exception`() = runTest {
        // Arrange
        every { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        val errorMessage = "Failed to delete"
        coEvery { soundscapeRepository.deleteCategory(any()) } throws RuntimeException(errorMessage)

        // Act
        viewModel.deleteCategory(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeLibraryUiState.Error::class.java)
        assertThat((state as SoundscapeLibraryUiState.Error).message).isEqualTo(errorMessage)
    }

    @Test
    fun `clearError reloads categories when state is Error`() = runTest {
        // Arrange
        every { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())
        viewModel = SoundscapeLibraryViewModel(soundscapeRepository)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { soundscapeRepository.deleteCategory(any()) } throws RuntimeException("Error")
        viewModel.deleteCategory(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(SoundscapeLibraryUiState.Success::class.java)
    }

    @Test
    fun `CategoryWithTrackCounts totalTracks sums all intensity levels`() {
        // Arrange
        val categoryWithCounts = CategoryWithTrackCounts(
            category = sampleCategory1,
            levelICounts = 3,
            levelIICounts = 5,
            levelIIICounts = 2
        )

        // Act & Assert
        assertThat(categoryWithCounts.totalTracks).isEqualTo(10)
    }

    @Test
    fun `CategoryWithTrackCounts totalTracks is zero when no tracks`() {
        // Arrange
        val categoryWithCounts = CategoryWithTrackCounts(
            category = sampleCategory1,
            levelICounts = 0,
            levelIICounts = 0,
            levelIIICounts = 0
        )

        // Act & Assert
        assertThat(categoryWithCounts.totalTracks).isEqualTo(0)
    }
}
