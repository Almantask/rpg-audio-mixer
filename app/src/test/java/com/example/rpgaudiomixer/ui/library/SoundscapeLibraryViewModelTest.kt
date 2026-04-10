package com.example.rpgaudiomixer.ui.library

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class SoundscapeLibraryViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun createCategory_emits_navigation_to_the_new_composer() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSoundscapeRepository()
        val viewModel = SoundscapeLibraryViewModel(repository)

        // Act
        viewModel.createCategory("  Arcane  ")
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(SoundscapeLibraryUiState.Success::class.java)
        assertThat(viewModel.navigationEvents.replayCache).containsExactly(
            SoundscapeLibraryNavigation.OpenComposer(categoryId = 1L, categoryName = "Arcane"),
        )
    }

    @Test
    fun downloadDemoSoundscapes_hides_the_demo_button_after_completion() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSoundscapeRepository()
        val viewModel = SoundscapeLibraryViewModel(repository)

        // Act
        viewModel.downloadDemoSoundscapes()
        advanceUntilIdle()

        // Assert
        val successState = viewModel.uiState.value as SoundscapeLibraryUiState.Success
        assertThat(successState.isDemoDownloadVisible).isFalse()
    }

    private class FakeSoundscapeRepository : SoundscapeRepository {
        private val categoriesFlow = MutableStateFlow<List<SoundscapeCategory>>(emptyList())
        private val tracksByCategory = mutableMapOf<Long, MutableStateFlow<List<SoundscapeTrack>>>()
        private var nextCategoryId = 1L

        override fun observeCategories(): Flow<List<SoundscapeCategory>> = categoriesFlow

        override fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?> {
            return categoriesFlow.map { categories -> categories.firstOrNull { it.id == categoryId } }
        }

        override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> {
            return tracksByCategory.getOrPut(categoryId) { MutableStateFlow(emptyList()) }
        }

        override fun observeMostPlayedTrack(): Flow<com.example.rpgaudiomixer.domain.model.MostPlayedSoundscapeTrack?> {
            return MutableStateFlow(null)
        }

        override suspend fun createCategory(name: String): Long {
            val categoryId = nextCategoryId++
            categoriesFlow.value = categoriesFlow.value + SoundscapeCategory(
                id = categoryId,
                name = name.trim(),
                themeLabel = null,
                iconResId = null,
                isDemoContent = false,
                levelOneCount = 0,
                levelTwoCount = 0,
                levelThreeCount = 0,
            )
            return categoryId
        }

        override suspend fun deleteCategory(categoryId: Long, deletedAtMillis: Long) {
            categoriesFlow.value = categoriesFlow.value.filterNot { it.id == categoryId }
        }

        override suspend fun saveTracks(categoryId: Long, tracks: List<SoundscapeTrack>) {
            tracksByCategory.getOrPut(categoryId) { MutableStateFlow(emptyList()) }.value = tracks
        }

        override suspend fun installDemoSoundscapes() {
            val demoCategories = listOf("Weather", "Interior", "Monsters", "City", "Dungeon")
            categoriesFlow.value = demoCategories.mapIndexed { index, name ->
                SoundscapeCategory(
                    id = (index + 1).toLong(),
                    name = name,
                    themeLabel = "Demo",
                    iconResId = null,
                    isDemoContent = true,
                    levelOneCount = 7,
                    levelTwoCount = 7,
                    levelThreeCount = 6,
                )
            }
        }
    }
}
