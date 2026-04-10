package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.media.FakeTrackFactory
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.scene.SceneSoundscapeRepository
import com.example.rpgaudiomixer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveSceneSoundscapesViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun playCategory_plays_a_track_from_the_selected_intensity_pool() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneSoundscapeRepository(
            linkedSoundscapes = listOf(
                sceneSoundscape(
                    categoryId = 7L,
                    categoryName = "Weather",
                    intensityLevel = IntensityLevel.II,
                ),
            ),
            tracksByCategory = mapOf(
                7L to listOf(
                    soundscapeTrack(id = 1L, filePath = "drizzle", intensityLevel = IntensityLevel.I),
                    soundscapeTrack(id = 2L, filePath = "storm", intensityLevel = IntensityLevel.II),
                ),
            ),
        )
        val trackFactory = FakeTrackFactory()
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneSoundscapeRepository = repository,
            sceneAudioEngine = SceneAudioEngine(trackFactory = trackFactory),
        )
        advanceUntilIdle()

        // Act
        viewModel.playCategory(7L)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundscapesUiState.Success
        val category = state.soundscapes.single()
        assertThat(category.currentTrackName).isEqualTo("storm")
        assertThat(category.isPlaying).isEqualTo(true)
        assertThat(repository.incrementedTrackIds).containsExactly(2L)
    }

    @Test
    fun playCategory_with_no_tracks_at_the_selected_intensity_emits_a_warning_message() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneSoundscapeRepository(
            linkedSoundscapes = listOf(
                sceneSoundscape(
                    categoryId = 7L,
                    categoryName = "Dungeon",
                    intensityLevel = IntensityLevel.III,
                ),
            ),
            tracksByCategory = mapOf(
                7L to listOf(
                    soundscapeTrack(id = 1L, filePath = "drip", intensityLevel = IntensityLevel.I),
                ),
            ),
        )
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneSoundscapeRepository = repository,
            sceneAudioEngine = SceneAudioEngine(trackFactory = FakeTrackFactory()),
        )
        advanceUntilIdle()

        // Act
        viewModel.playCategory(7L)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.errorMessage.value).isEqualTo("No tracks are available at intensity III for Dungeon.")
    }

    @Test
    fun addCategory_imports_the_soundscape_and_removes_it_from_the_picker_options() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneSoundscapeRepository(
            linkedSoundscapes = emptyList(),
            availableCategories = listOf(
                soundscapeCategory(id = 7L, name = "Weather"),
                soundscapeCategory(id = 8L, name = "Interior"),
            ),
        )
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneSoundscapeRepository = repository,
            sceneAudioEngine = SceneAudioEngine(trackFactory = FakeTrackFactory()),
        )
        advanceUntilIdle()

        // Act
        viewModel.addCategory(7L)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundscapesUiState.Success
        assertThat(state.soundscapes.map { it.categoryName }).isEqualTo(listOf("Weather"))
        assertThat(state.availableCategoriesToAdd.map { it.name }).isEqualTo(listOf("Interior"))
        assertThat(state.availableCategoriesToAdd.single().totalPlayCount).isEqualTo(142)
    }

    @Test
    fun setMix_updates_the_category_mix_and_active_player_output() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneSoundscapeRepository(
            linkedSoundscapes = listOf(sceneSoundscape(categoryId = 7L, categoryName = "Weather")),
            tracksByCategory = mapOf(
                7L to listOf(soundscapeTrack(id = 1L, filePath = "rain", intensityLevel = IntensityLevel.I)),
            ),
        )
        val trackFactory = FakeTrackFactory()
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneSoundscapeRepository = repository,
            sceneAudioEngine = SceneAudioEngine(trackFactory = trackFactory),
        )
        advanceUntilIdle()
        viewModel.playCategory(7L)
        advanceUntilIdle()

        // Act
        viewModel.setMix(categoryId = 7L, mixVolume = 0.3f)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundscapesUiState.Success
        assertThat(state.soundscapes.single().mixVolume).isEqualTo(0.3f)
        assertThat(trackFactory.loopPlayers.single().volumeHistory.last()).isEqualTo(0.3f)
    }

    @Test
    fun init_with_autoplay_starts_scene_playback_for_linked_soundscapes() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneSoundscapeRepository(
            linkedSoundscapes = listOf(
                sceneSoundscape(
                    categoryId = 7L,
                    categoryName = "Weather",
                    intensityLevel = IntensityLevel.II,
                ),
            ),
            tracksByCategory = mapOf(
                7L to listOf(
                    soundscapeTrack(id = 2L, filePath = "storm", intensityLevel = IntensityLevel.II),
                ),
            ),
        )
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 5L,
            autoplay = true,
            sceneRepository = FakeSceneRepository(),
            sceneSoundscapeRepository = repository,
            sceneAudioEngine = SceneAudioEngine(trackFactory = FakeTrackFactory()),
        )

        // Act
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundscapesUiState.Success
        assertThat(state.soundscapes.single().isPlaying).isEqualTo(true)
        assertThat(state.soundscapes.single().currentTrackName).isEqualTo("storm")
    }

    @Test
    fun init_without_autoplay_leaves_the_current_scene_playing_in_the_background() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val sharedEngine = SceneAudioEngine(trackFactory = trackFactory)
        ActiveSceneSoundscapesViewModel(
            sceneId = 5L,
            autoplay = true,
            sceneRepository = FakeSceneRepository(),
            sceneSoundscapeRepository = FakeSceneSoundscapeRepository(
                linkedSoundscapes = listOf(sceneSoundscape(categoryId = 7L, categoryName = "Tavern")),
                tracksByCategory = mapOf(
                    7L to listOf(soundscapeTrack(id = 1L, filePath = "tavern-loop", intensityLevel = IntensityLevel.I)),
                ),
            ),
            sceneAudioEngine = sharedEngine,
        )
        advanceUntilIdle()

        // Act
        ActiveSceneSoundscapesViewModel(
            sceneId = 6L,
            autoplay = false,
            sceneRepository = FakeSceneRepository(),
            sceneSoundscapeRepository = FakeSceneSoundscapeRepository(),
            sceneAudioEngine = sharedEngine,
        )
        advanceUntilIdle()

        // Assert
        assertThat(sharedEngine.activeSceneId).isEqualTo(5L)
        assertThat(trackFactory.createdLoopTracks).containsExactly("tavern-loop")
    }

    @Test
    fun saved_master_volume_is_loaded_immediately_into_the_ui_state() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 5L,
            autoplay = false,
            sceneRepository = FakeSceneRepository(masterVolume = 0.7f),
            sceneSoundscapeRepository = FakeSceneSoundscapeRepository(),
            sceneAudioEngine = SceneAudioEngine(trackFactory = FakeTrackFactory()),
        )

        // Act
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundscapesUiState.Success
        assertThat(state.masterVolume).isEqualTo(0.7f)
    }

    @Test
    fun soundscape_with_no_tracks_disables_playback_controls_but_keeps_mix_available() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 5L,
            autoplay = false,
            sceneRepository = FakeSceneRepository(),
            sceneSoundscapeRepository = FakeSceneSoundscapeRepository(
                linkedSoundscapes = listOf(sceneSoundscape(categoryId = 7L, categoryName = "Empty Vault")),
                tracksByCategory = emptyMap(),
            ),
            sceneAudioEngine = SceneAudioEngine(trackFactory = FakeTrackFactory()),
        )

        // Act
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundscapesUiState.Success
        assertThat(state.soundscapes.single().canStartPlayback).isEqualTo(false)
        assertThat(state.soundscapes.single().availableIntensityLevels).isEmpty()
        assertThat(state.soundscapes.single().mixVolume).isEqualTo(1f)
    }

    @Test
    fun reorderCategories_persists_the_new_display_order() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneSoundscapeRepository(
            linkedSoundscapes = listOf(
                sceneSoundscape(categoryId = 7L, categoryName = "Weather"),
                sceneSoundscape(categoryId = 8L, categoryName = "Interior"),
            ),
        )
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 5L,
            autoplay = false,
            sceneRepository = FakeSceneRepository(),
            sceneSoundscapeRepository = repository,
            sceneAudioEngine = SceneAudioEngine(trackFactory = FakeTrackFactory()),
        )
        advanceUntilIdle()

        // Act
        viewModel.reorderCategories(listOf(8L, 7L))
        advanceUntilIdle()

        // Assert
        assertThat(repository.lastReorderedCategoryIds).isEqualTo(listOf(8L, 7L))
    }

    private class FakeSceneRepository(
        private val masterVolume: Float = 1f,
    ) : SceneRepository {
        override fun observeScenes(): Flow<List<Scene>> = flowOf(emptyList())

        override fun observeScene(sceneId: Long): Flow<Scene?> {
            return flowOf(
                Scene(
                    id = sceneId,
                    name = "Moonlit Keep",
                    description = null,
                    tags = emptyList(),
                    masterVolume = masterVolume,
                ),
            )
        }

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long {
            error("Not needed in this test")
        }

        override suspend fun deleteScene(sceneId: Long, deletedAtMillis: Long) {
            error("Not needed in this test")
        }

        override suspend fun updateMasterVolume(sceneId: Long, masterVolume: Float) {
            error("Not needed in this test")
        }
    }

    private class FakeSceneSoundscapeRepository(
        linkedSoundscapes: List<SceneSoundscape> = emptyList(),
        availableCategories: List<SoundscapeCategory> = emptyList(),
        private val tracksByCategory: Map<Long, List<SoundscapeTrack>> = emptyMap(),
    ) : SceneSoundscapeRepository {
        private val linkedFlow = MutableStateFlow(linkedSoundscapes)
        private val availableFlow = MutableStateFlow(availableCategories)
        val incrementedTrackIds = mutableListOf<Long>()
        var lastReorderedCategoryIds: List<Long> = emptyList()

        override fun observeSceneSoundscapes(sceneId: Long): Flow<List<SceneSoundscape>> = linkedFlow

        override fun observeAvailableSoundscapes(sceneId: Long): Flow<List<SoundscapeCategory>> = availableFlow

        override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> {
            return flowOf(tracksByCategory[categoryId].orEmpty())
        }

        override suspend fun addSoundscapeToScene(sceneId: Long, categoryId: Long) {
            val category = availableFlow.value.first { it.id == categoryId }
            linkedFlow.value = linkedFlow.value + sceneSoundscape(
                categoryId = categoryId,
                categoryName = category.name,
            ).copy(
                levelOneCount = category.levelOneCount,
                levelTwoCount = category.levelTwoCount,
                levelThreeCount = category.levelThreeCount,
                displayOrder = linkedFlow.value.size,
            )
            availableFlow.value = availableFlow.value.filterNot { it.id == categoryId }
        }

        override suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long) {
            linkedFlow.value = linkedFlow.value.filterNot { it.categoryId == categoryId }
        }

        override suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolume: Float) {
            linkedFlow.value = linkedFlow.value.map { soundscape ->
                if (soundscape.categoryId == categoryId) soundscape.copy(mixVolume = mixVolume) else soundscape
            }
        }

        override suspend fun updateIntensityLevel(sceneId: Long, categoryId: Long, intensityLevel: IntensityLevel) {
            linkedFlow.value = linkedFlow.value.map { soundscape ->
                if (soundscape.categoryId == categoryId) {
                    soundscape.copy(intensityLevel = intensityLevel)
                } else {
                    soundscape
                }
            }
        }

        override suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>) {
            lastReorderedCategoryIds = orderedCategoryIds
            linkedFlow.value = orderedCategoryIds.mapIndexedNotNull { index, categoryId ->
                linkedFlow.value.firstOrNull { it.categoryId == categoryId }?.copy(displayOrder = index)
            }
        }

        override suspend fun incrementTrackPlayCount(trackId: Long) {
            incrementedTrackIds += trackId
        }
    }
}

private fun sceneSoundscape(
    categoryId: Long,
    categoryName: String,
    intensityLevel: IntensityLevel = IntensityLevel.I,
): SceneSoundscape {
    return SceneSoundscape(
        sceneId = 5L,
        categoryId = categoryId,
        categoryName = categoryName,
        themeLabel = null,
        iconResId = null,
        isDemoContent = false,
        mixVolume = 1f,
        intensityLevel = intensityLevel,
        displayOrder = 0,
        levelOneCount = 1,
        levelTwoCount = 1,
        levelThreeCount = 1,
    )
}

private fun soundscapeTrack(
    id: Long,
    filePath: String,
    intensityLevel: IntensityLevel,
): SoundscapeTrack {
    return SoundscapeTrack(
        id = id,
        categoryId = 7L,
        name = filePath,
        filePath = filePath,
        intensityLevel = intensityLevel,
        mixVolumePercent = 100,
        displayOrder = 0,
    )
}

private fun soundscapeCategory(
    id: Long,
    name: String,
): SoundscapeCategory {
    return SoundscapeCategory(
        id = id,
        name = name,
        themeLabel = null,
        iconResId = null,
        isDemoContent = false,
        levelOneCount = 1,
        levelTwoCount = 1,
        levelThreeCount = 1,
        totalPlayCount = if (id == 8L) 142 else 0,
    )
}
