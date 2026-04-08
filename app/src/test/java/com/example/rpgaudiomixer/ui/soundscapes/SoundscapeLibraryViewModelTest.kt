package com.example.rpgaudiomixer.ui.soundscapes

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoundscapeLibraryViewModelTest {

    @Test
    fun init_exposes_categories_and_demo_download_state() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        repository.categoriesFlow.value = listOf(
            SoundscapeCategory(
                id = 7L,
                name = "Weather",
                iconResId = null,
                themeLabel = "Environment",
                levelOneTrackCount = 3,
                levelTwoTrackCount = 5,
                levelThreeTrackCount = 2,
            )
        )
        repository.demoContentAvailableFlow.value = false

        // Act
        val viewModel = SoundscapeLibraryViewModel(
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(
            SoundscapeLibraryUiState(
                isLoading = false,
                categories = listOf(
                    SoundscapeCategory(
                        id = 7L,
                        name = "Weather",
                        iconResId = null,
                        themeLabel = "Environment",
                        levelOneTrackCount = 3,
                        levelTwoTrackCount = 5,
                        levelThreeTrackCount = 2,
                    )
                ),
                showDemoButton = true,
            )
        )
    }

    @Test
    fun createCategory_trims_name_and_requests_navigation_to_the_created_category() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        val viewModel = SoundscapeLibraryViewModel(
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.createCategory("  Arcane  ")
        advanceUntilIdle()

        // Assert
        assertThat(repository.createdCategoryNames).containsExactly("Arcane")
        assertThat(viewModel.uiState.value.pendingComposerCategoryId).isEqualTo(91L)
    }

    @Test
    fun createCategory_ignores_blank_names() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        val viewModel = SoundscapeLibraryViewModel(
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.createCategory("   ")
        advanceUntilIdle()

        // Assert
        assertThat(repository.createdCategoryNames).isEmpty()
    }

    @Test
    fun deleteCategory_delegates_to_the_repository() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        val viewModel = SoundscapeLibraryViewModel(
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.deleteCategory(4L)
        advanceUntilIdle()

        // Assert
        assertThat(repository.deletedCategoryIds).containsExactly(4L)
    }

    @Test
    fun downloadDemoSoundscapes_marks_demo_button_hidden_after_completion() = runTest {
        // Arrange
        val repository = FakeSoundscapeRepository()
        val viewModel = SoundscapeLibraryViewModel(
            soundscapeRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.downloadDemoSoundscapes()
        advanceUntilIdle()

        // Assert
        assertThat(repository.demoSeeded).isTrue()
        assertThat(viewModel.uiState.value.showDemoButton).isFalse()
        assertThat(viewModel.uiState.value.isDownloadingDemo).isFalse()
    }

    private class FakeSoundscapeRepository : SoundscapeRepository {
        val categoriesFlow = MutableStateFlow<List<SoundscapeCategory>>(emptyList())
        val demoContentAvailableFlow = MutableStateFlow(false)

        val createdCategoryNames = mutableListOf<String>()
        val deletedCategoryIds = mutableListOf<Long>()
        var demoSeeded = false

        override fun observeCategories(): Flow<List<SoundscapeCategory>> = categoriesFlow

        override fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?> = MutableStateFlow(null)

        override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> = MutableStateFlow(emptyList())

        override fun observeHasDemoSoundscapes(): Flow<Boolean> = demoContentAvailableFlow

        override suspend fun createCategory(name: String): Long {
            createdCategoryNames += name
            return 91L
        }

        override suspend fun deleteCategory(categoryId: Long) {
            deletedCategoryIds += categoryId
        }

        override suspend fun importTrack(categoryId: Long, sourceUri: String): SoundscapeTrack {
            error("Not needed in this test")
        }

        override suspend fun saveTracks(categoryId: Long, tracks: List<SoundscapeTrack>) = Unit

        override suspend fun seedDemoSoundscapes() {
            demoSeeded = true
            demoContentAvailableFlow.value = true
        }
    }
}
