package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.media.SceneAudioController
import com.example.rpgaudiomixer.domain.media.ScenePlaybackRequest
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ActiveSceneSoundscapesViewModelTest {

    @Test
    fun init_exposes_scene_and_ordered_soundscape_cards() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", "Night watch", emptyList(), 2)
            sceneSoundscapesFlow.value = listOf(
                sceneSoundscape(categoryId = 20L, name = "Interior", displayOrder = 1),
                sceneSoundscape(categoryId = 10L, name = "Weather", displayOrder = 0),
            )
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            tracksByCategory[10L] = MutableStateFlow(
                listOf(
                    track(id = 1L, categoryId = 10L, name = "Light Rain", intensityLevel = IntensityLevel.I),
                    track(id = 2L, categoryId = 10L, name = "Storm", intensityLevel = IntensityLevel.II),
                )
            )
            tracksByCategory[20L] = MutableStateFlow(
                listOf(track(id = 3L, categoryId = 20L, name = "Crowd", intensityLevel = IntensityLevel.I))
            )
        }
        val audioController = FakeSceneAudioController()

        // Act
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.sceneName).isEqualTo("Forest Path")
        assertThat(viewModel.uiState.value.soundscapes.map { it.category.name }).containsExactly("Weather", "Interior")
        assertThat(viewModel.uiState.value.soundscapes.first().availableIntensityLevels).containsExactly(
            IntensityLevel.I,
            IntensityLevel.II,
        )
    }

    @Test
    fun init_marks_a_category_with_no_tracks_as_unavailable_for_playback() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", "Night watch", emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(
                sceneSoundscape(
                    categoryId = 10L,
                    name = "Weather",
                    levelOneTrackCount = 0,
                    levelTwoTrackCount = 0,
                    levelThreeTrackCount = 0,
                )
            )
        }
        val soundscapeRepository = FakeSoundscapeRepository()

        // Act
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = FakeSceneAudioController(),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.soundscapes.single().hasAvailableTracks).isFalse()
        assertThat(viewModel.uiState.value.soundscapes.single().availableIntensityLevels).isEmpty()
    }

    @Test
    fun playCategory_uses_the_selected_intensity_pool_and_updates_the_current_track() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(
                sceneSoundscape(
                    categoryId = 10L,
                    name = "Weather",
                    intensityLevel = IntensityLevel.II,
                )
            )
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            tracksByCategory[10L] = MutableStateFlow(
                listOf(
                    track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I),
                    track(id = 2L, categoryId = 10L, name = "Storm", intensityLevel = IntensityLevel.II),
                )
            )
        }
        val audioController = FakeSceneAudioController().apply {
            nextRolledTrack = track(id = 2L, categoryId = 10L, name = "Storm", intensityLevel = IntensityLevel.II)
        }
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.playCategory(10L)
        advanceUntilIdle()

        // Assert
        assertThat(audioController.rolledPools.single().map { it.name }).containsExactly("Storm")
        assertThat(viewModel.uiState.value.soundscapes.single().currentTrackName).isEqualTo("Storm")
        assertThat(viewModel.uiState.value.soundscapes.single().isPlaying).isTrue()
    }

    @Test
    fun playCategory_increments_the_selected_track_play_count() = runTest {
        // Arrange
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            categoriesFlow.value = listOf(category(id = 10L, name = "Weather", levelOneTrackCount = 1))
            tracksByCategory[10L] = MutableStateFlow(
                listOf(track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I))
            )
        }
        val audioController = FakeSceneAudioController().apply {
            nextRolledTrack = track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I)
        }
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = FakeSceneRepository().apply {
                sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
                sceneSoundscapesFlow.value = listOf(sceneSoundscape(categoryId = 10L, name = "Weather"))
            },
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.playCategory(10L)
        advanceUntilIdle()

        // Assert
        assertThat(soundscapeRepository.incrementedTrackIds).containsExactly(1L)
    }

    @Test
    fun init_with_autoplay_switches_to_the_scene_and_marks_loaded_soundscapes_playing() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(
                sceneSoundscape(
                    categoryId = 10L,
                    name = "Weather",
                    intensityLevel = IntensityLevel.II,
                )
            )
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            tracksByCategory[10L] = MutableStateFlow(
                listOf(
                    track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I),
                    track(id = 2L, categoryId = 10L, name = "Storm", intensityLevel = IntensityLevel.II),
                )
            )
        }
        val audioController = FakeSceneAudioController()

        // Act
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = true,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(audioController.switchRequests).containsExactly(
            SceneSwitchRequest(
                sceneId = 4L,
                categories = listOf(
                    ScenePlaybackRequest(
                        categoryId = 10L,
                        trackPath = "/tracks/Storm.mp3",
                        mixVolume = 1f,
                    )
                ),
            )
        )
        assertThat(viewModel.uiState.value.soundscapes.single().currentTrackName).isEqualTo("Storm")
        assertThat(viewModel.uiState.value.soundscapes.single().isPlaying).isTrue()
    }

    @Test
    fun init_without_autoplay_does_not_replace_the_currently_playing_scene() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(sceneSoundscape(categoryId = 10L, name = "Weather"))
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            tracksByCategory[10L] = MutableStateFlow(
                listOf(track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I))
            )
        }
        val audioController = FakeSceneAudioController().apply {
            activeSceneId = 99L
        }

        // Act
        ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(audioController.switchRequests).isEmpty()
        assertThat(audioController.removedCategoryIds).isEmpty()
    }

    @Test
    fun init_with_autoplay_shows_an_error_when_no_matching_tracks_are_available() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(
                sceneSoundscape(
                    categoryId = 10L,
                    name = "Weather",
                    intensityLevel = IntensityLevel.III,
                )
            )
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            tracksByCategory[10L] = MutableStateFlow(
                listOf(track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I))
            )
        }

        // Act
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = true,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = FakeSceneAudioController(),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("No tracks are available to autoplay this scene.")
    }

    @Test
    fun playCategory_resumes_a_paused_track_instead_of_rolling_a_new_one() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(sceneSoundscape(categoryId = 10L, name = "Weather"))
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            tracksByCategory[10L] = MutableStateFlow(
                listOf(track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I))
            )
        }
        val audioController = FakeSceneAudioController()
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()
        viewModel.rollRandom(10L)
        advanceUntilIdle()
        viewModel.pauseCategory(10L)
        advanceUntilIdle()

        // Act
        viewModel.playCategory(10L)
        advanceUntilIdle()

        // Assert
        assertThat(audioController.resumedCategoryIds).containsExactly(10L)
        assertThat(audioController.rolledPools).hasSize(1)
        assertThat(viewModel.uiState.value.soundscapes.single().isPlaying).isTrue()
    }

    @Test
    fun rollRandom_shows_an_error_when_the_selected_intensity_pool_is_empty() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(
                sceneSoundscape(categoryId = 10L, name = "Dungeon", intensityLevel = IntensityLevel.II)
            )
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            tracksByCategory[10L] = MutableStateFlow(
                listOf(track(id = 1L, categoryId = 10L, name = "Drip", intensityLevel = IntensityLevel.I))
            )
        }
        val audioController = FakeSceneAudioController()
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.rollRandom(10L)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("No tracks are available at intensity II.")
    }

    @Test
    fun setMasterVolume_updates_ui_state_and_delegates_to_the_audio_controller() = runTest {
        // Arrange
        val audioController = FakeSceneAudioController()
        val viewModel = buildViewModel(
            testScheduler = testScheduler,
            audioController = audioController,
        )

        // Act
        viewModel.setMasterVolume(0.4f)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.masterVolume).isEqualTo(0.4f)
        assertThat(audioController.masterVolumes).containsExactly(0.4f)
    }

    @Test
    fun setMix_updates_the_card_state_persists_it_and_updates_the_audio_controller() = runTest {
        // Arrange
        val repository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(sceneSoundscape(categoryId = 10L, name = "Weather"))
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            categoriesFlow.value = listOf(
                category(id = 10L, name = "Weather", levelOneTrackCount = 1),
                category(id = 20L, name = "Interior", levelOneTrackCount = 2),
            )
            tracksByCategory[10L] = MutableStateFlow(
                listOf(track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I))
            )
            tracksByCategory[20L] = MutableStateFlow(
                listOf(track(id = 2L, categoryId = 20L, name = "Crowd", intensityLevel = IntensityLevel.I))
            )
        }
        val audioController = FakeSceneAudioController()
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = repository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.setMix(categoryId = 10L, mixVolume = 0.3f)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.soundscapes.single().mixVolume).isEqualTo(0.3f)
        assertThat(repository.updatedRequests.single().mixVolume).isEqualTo(0.3f)
        assertThat(audioController.mixUpdates.single()).isEqualTo(10L to 0.3f)
    }

    @Test
    fun setIntensity_updates_the_card_state_and_persists_it() = runTest {
        // Arrange
        val repository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(sceneSoundscape(categoryId = 10L, name = "Weather"))
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            categoriesFlow.value = listOf(
                category(id = 10L, name = "Weather", levelOneTrackCount = 1, levelTwoTrackCount = 1),
                category(id = 20L, name = "Interior", levelOneTrackCount = 2),
            )
            tracksByCategory[10L] = MutableStateFlow(
                listOf(
                    track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I),
                    track(id = 2L, categoryId = 10L, name = "Storm", intensityLevel = IntensityLevel.II),
                )
            )
            tracksByCategory[20L] = MutableStateFlow(
                listOf(track(id = 3L, categoryId = 20L, name = "Crowd", intensityLevel = IntensityLevel.I))
            )
        }
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = repository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = FakeSceneAudioController(),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.setIntensity(categoryId = 10L, level = IntensityLevel.II)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.soundscapes.single().selectedIntensityLevel).isEqualTo(IntensityLevel.II)
        assertThat(repository.updatedRequests.single().intensityLevel).isEqualTo(IntensityLevel.II)
    }

    @Test
    fun addCategory_adds_the_soundscape_to_the_scene_and_marks_it_added_in_the_picker_state() = runTest {
        // Arrange
        val repository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(sceneSoundscape(categoryId = 10L, name = "Weather"))
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            categoriesFlow.value = listOf(
                category(id = 10L, name = "Weather", levelOneTrackCount = 1),
                category(id = 20L, name = "Interior", levelOneTrackCount = 2),
            )
            tracksByCategory[10L] = MutableStateFlow(
                listOf(track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I))
            )
            tracksByCategory[20L] = MutableStateFlow(
                listOf(track(id = 2L, categoryId = 20L, name = "Crowd", intensityLevel = IntensityLevel.I))
            )
        }
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = repository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = FakeSceneAudioController(),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.showAddSoundscapeSheet()
        viewModel.addCategory(20L)
        advanceUntilIdle()

        // Assert
        assertThat(repository.addedSoundscapes).containsExactly(4L to 20L)
        assertThat(viewModel.uiState.value.selectionOptions.single { it.category.id == 20L }.isAdded).isTrue()
    }

    @Test
    fun init_exposes_total_play_count_for_each_soundscape_selection_option() = runTest {
        // Arrange
        val repository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(sceneSoundscape(categoryId = 10L, name = "Weather"))
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            categoriesFlow.value = listOf(
                category(id = 10L, name = "Weather", levelOneTrackCount = 2),
                category(id = 20L, name = "Interior", levelOneTrackCount = 1),
            )
            tracksByCategory[10L] = MutableStateFlow(
                listOf(
                    track(id = 1L, categoryId = 10L, name = "Rain", intensityLevel = IntensityLevel.I, playCount = 4),
                    track(id = 2L, categoryId = 10L, name = "Storm", intensityLevel = IntensityLevel.I, playCount = 7),
                )
            )
            tracksByCategory[20L] = MutableStateFlow(
                listOf(track(id = 3L, categoryId = 20L, name = "Crowd", intensityLevel = IntensityLevel.I, playCount = 3))
            )
        }

        // Act
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = repository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = FakeSceneAudioController(),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.selectionOptions.associate { it.category.id to it.totalPlayCount }).isEqualTo(
            mapOf(
                10L to 11,
                20L to 3,
            )
        )
    }

    @Test
    fun removeCategory_updates_the_repository_and_releases_audio_for_that_category() = runTest {
        // Arrange
        val repository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(sceneSoundscape(categoryId = 10L, name = "Weather"))
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            categoriesFlow.value = listOf(
                category(id = 10L, name = "Weather", levelOneTrackCount = 1),
                category(id = 20L, name = "Interior", levelOneTrackCount = 2),
            )
            tracksByCategory[10L] = MutableStateFlow(
                listOf(track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I))
            )
            tracksByCategory[20L] = MutableStateFlow(
                listOf(track(id = 2L, categoryId = 20L, name = "Crowd", intensityLevel = IntensityLevel.I))
            )
        }
        val audioController = FakeSceneAudioController()
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = repository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.removeCategory(10L)
        advanceUntilIdle()

        // Assert
        assertThat(repository.removedSoundscapes).containsExactly(4L to 10L)
        assertThat(audioController.removedCategoryIds).containsExactly(10L)
    }

    @Test
    fun reorderCategories_persists_the_new_sequence() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 2)
            sceneSoundscapesFlow.value = listOf(
                sceneSoundscape(categoryId = 10L, name = "Weather", displayOrder = 0),
                sceneSoundscape(categoryId = 20L, name = "Interior", displayOrder = 1),
            )
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            tracksByCategory[10L] = MutableStateFlow(listOf(track(id = 1L, categoryId = 10L, name = "Rain", intensityLevel = IntensityLevel.I)))
            tracksByCategory[20L] = MutableStateFlow(listOf(track(id = 2L, categoryId = 20L, name = "Crowd", intensityLevel = IntensityLevel.I)))
        }
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = FakeSceneAudioController(),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.reorderCategories(listOf(20L, 10L))
        advanceUntilIdle()

        // Assert
        assertThat(sceneRepository.reorderedCategoryIds).containsExactly(listOf(20L, 10L))
    }

    private fun buildViewModel(
        testScheduler: TestCoroutineScheduler,
        audioController: FakeSceneAudioController = FakeSceneAudioController(),
    ): ActiveSceneSoundscapesViewModel {
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 1)
            sceneSoundscapesFlow.value = listOf(sceneSoundscape(categoryId = 10L, name = "Weather"))
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            categoriesFlow.value = listOf(
                category(id = 10L, name = "Weather", levelOneTrackCount = 1),
                category(id = 20L, name = "Interior", levelOneTrackCount = 2),
            )
            tracksByCategory[10L] = MutableStateFlow(
                listOf(track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I))
            )
            tracksByCategory[20L] = MutableStateFlow(
                listOf(track(id = 2L, categoryId = 20L, name = "Crowd", intensityLevel = IntensityLevel.I))
            )
        }
        audioController.apply {
            nextRolledTrack = track(id = 1L, categoryId = 10L, name = "Drizzle", intensityLevel = IntensityLevel.I)
        }
        return ActiveSceneSoundscapesViewModel(
            sceneId = 4L,
            autoplay = false,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            sceneAudioController = audioController,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
    }

    private fun sceneSoundscape(
        categoryId: Long,
        name: String,
        displayOrder: Int = 0,
        mixVolume: Float = 1f,
        intensityLevel: IntensityLevel = IntensityLevel.I,
        levelOneTrackCount: Int = 1,
        levelTwoTrackCount: Int = 0,
        levelThreeTrackCount: Int = 0,
    ) = SceneSoundscape(
        sceneId = 4L,
        category = category(
            id = categoryId,
            name = name,
            levelOneTrackCount = levelOneTrackCount,
            levelTwoTrackCount = levelTwoTrackCount,
            levelThreeTrackCount = levelThreeTrackCount,
        ),
        displayOrder = displayOrder,
        mixVolume = mixVolume,
        intensityLevel = intensityLevel,
    )

    private fun category(
        id: Long,
        name: String,
        levelOneTrackCount: Int = 0,
        levelTwoTrackCount: Int = 0,
        levelThreeTrackCount: Int = 0,
    ) = SoundscapeCategory(
        id = id,
        name = name,
        iconResId = null,
        themeLabel = null,
        levelOneTrackCount = levelOneTrackCount,
        levelTwoTrackCount = levelTwoTrackCount,
        levelThreeTrackCount = levelThreeTrackCount,
    )

    private fun track(
        id: Long,
        categoryId: Long,
        name: String,
        intensityLevel: IntensityLevel,
        playCount: Int = 0,
    ) = SoundscapeTrack(
        id = id,
        categoryId = categoryId,
        name = name,
        filePath = "/tracks/$name.mp3",
        intensityLevel = intensityLevel,
        mixVolume = 1f,
        playCount = playCount,
    )

    private class FakeSceneRepository : SceneRepository {
        val sceneFlow = MutableStateFlow<Scene?>(null)
        val sceneSoundscapesFlow = MutableStateFlow<List<SceneSoundscape>>(emptyList())
        val addedSoundscapes = mutableListOf<Pair<Long, Long>>()
        val removedSoundscapes = mutableListOf<Pair<Long, Long>>()
        val reorderedCategoryIds = mutableListOf<List<Long>>()
        val updatedRequests = mutableListOf<UpdateSceneSoundscapeRequest>()

        override fun observeScenes(): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeScene(sceneId: Long): Flow<Scene?> = sceneFlow

        override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeSoundscapesForScene(sceneId: Long): Flow<List<SceneSoundscape>> = sceneSoundscapesFlow

        override fun observeFxForScene(sceneId: Long): Flow<List<SceneFx>> = MutableStateFlow(emptyList())

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long = 0L

        override suspend fun cloneScene(sceneId: Long, name: String): Long = 0L

        override suspend fun updateScene(
            sceneId: Long,
            name: String,
            description: String?,
            tags: List<String>,
        ) = Unit

        override suspend fun deleteScene(sceneId: Long) = Unit

        override suspend fun linkScenesToSession(sessionId: Long, sceneIds: List<Long>) = Unit

        override suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long) = Unit

        override suspend fun addSoundscapeToScene(sceneId: Long, categoryId: Long) {
            addedSoundscapes += sceneId to categoryId
        }

        override suspend fun updateSoundscapeInScene(
            sceneId: Long,
            categoryId: Long,
            displayOrder: Int,
            mixVolume: Float,
            intensityLevel: IntensityLevel,
        ) {
            updatedRequests += UpdateSceneSoundscapeRequest(sceneId, categoryId, displayOrder, mixVolume, intensityLevel)
        }

        override suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>) {
            reorderedCategoryIds += orderedCategoryIds
        }

        override suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long) {
            removedSoundscapes += sceneId to categoryId
        }

        override suspend fun addFxToScene(sceneId: Long, fxTrackId: Long) = Unit

        override suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>) = Unit

        override suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long) = Unit
    }

    private class FakeSoundscapeRepository : SoundscapeRepository {
        val categoriesFlow = MutableStateFlow<List<SoundscapeCategory>>(emptyList())
        val tracksByCategory = mutableMapOf<Long, MutableStateFlow<List<SoundscapeTrack>>>()
        val incrementedTrackIds = mutableListOf<Long>()

        override fun observeCategories(): Flow<List<SoundscapeCategory>> = categoriesFlow

        override fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?> = MutableStateFlow(null)

        override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> =
            tracksByCategory.getOrPut(categoryId) { MutableStateFlow(emptyList()) }

        override fun observeHasDemoSoundscapes(): Flow<Boolean> = MutableStateFlow(false)

        override suspend fun createCategory(name: String): Long = 0L

        override suspend fun deleteCategory(categoryId: Long) = Unit

        override suspend fun importTrack(categoryId: Long, sourceUri: String): SoundscapeTrack =
            SoundscapeTrack(
                id = 0L,
                categoryId = categoryId,
                name = sourceUri.substringAfterLast('/'),
                filePath = sourceUri,
                intensityLevel = IntensityLevel.I,
                mixVolume = 1f,
            )

        override suspend fun saveTracks(categoryId: Long, tracks: List<SoundscapeTrack>) = Unit

        override suspend fun seedDemoSoundscapes() = Unit

        override suspend fun incrementTrackPlayCount(trackId: Long) {
            incrementedTrackIds += trackId
        }
    }

    private class FakeSceneAudioController : SceneAudioController {
        override var activeSceneId: Long? = null
        var nextRolledTrack: SoundscapeTrack? = null
        val rolledPools = mutableListOf<List<SoundscapeTrack>>()
        val resumedCategoryIds = mutableListOf<Long>()
        val masterVolumes = mutableListOf<Float>()
        val mixUpdates = mutableListOf<Pair<Long, Float>>()
        val removedCategoryIds = mutableListOf<Long>()
        val switchRequests = mutableListOf<SceneSwitchRequest>()

        override fun addCategory(categoryId: Long) = Unit

        override fun removeCategory(categoryId: Long) {
            removedCategoryIds += categoryId
        }

        override fun play(categoryId: Long, trackPath: String) = Unit

        override fun pause(categoryId: Long) = Unit

        override fun resume(categoryId: Long) {
            resumedCategoryIds += categoryId
        }

        override fun stop(categoryId: Long) = Unit

        override fun rollRandomTrack(categoryId: Long, pool: List<SoundscapeTrack>): SoundscapeTrack? {
            rolledPools += pool
            return nextRolledTrack ?: pool.firstOrNull()
        }

        override fun setCategoryMixVolume(categoryId: Long, mixVolume: Float) {
            mixUpdates += categoryId to mixVolume
        }

        override fun setMasterVolume(volume: Float) {
            masterVolumes += volume
        }

        override suspend fun switchToScene(newSceneId: Long, categories: List<ScenePlaybackRequest>) {
            activeSceneId = newSceneId
            switchRequests += SceneSwitchRequest(newSceneId, categories)
        }

        override fun releaseAll() = Unit
    }

    private data class UpdateSceneSoundscapeRequest(
        val sceneId: Long,
        val categoryId: Long,
        val displayOrder: Int,
        val mixVolume: Float,
        val intensityLevel: IntensityLevel,
    )

    private data class SceneSwitchRequest(
        val sceneId: Long,
        val categories: List<ScenePlaybackRequest>,
    )
}
