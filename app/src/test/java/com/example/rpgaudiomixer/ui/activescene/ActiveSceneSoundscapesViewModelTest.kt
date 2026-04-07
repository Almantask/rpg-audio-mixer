package com.example.rpgaudiomixer.ui.activescene

import app.cash.turbine.test
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.media.CategoryPlayer
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SceneSoundscapeRepository
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveSceneSoundscapesViewModelTest {

    private val sceneSoundscapeRepository: SceneSoundscapeRepository = mockk(relaxed = true)
    private val soundscapeRepository: SoundscapeRepository = mockk(relaxed = true)
    private val audioEngine: SceneAudioEngine = mockk(relaxed = true)

    private lateinit var viewModel: ActiveSceneSoundscapesViewModel

    private val testDispatcher = StandardTestDispatcher()

    private val sceneId = 1L
    private val categoryId = 100L
    private val categoryName = "Forest"

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        clearAllMocks()

        every { audioEngine.masterVolume } returns 1.0f

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneSoundscapeRepository,
            soundscapeRepository,
            audioEngine
        )
    }

    @Test
    fun loadScene_emits_loading_state_initially() = runTest {
        // Arrange
        val sceneSoundscapes = emptyList<SceneSoundscape>()
        every { sceneSoundscapeRepository.observeByScene(sceneId) } returns flowOf(sceneSoundscapes)

        // Act & Assert
        viewModel.uiState.test {
            // Assert
            assertThat(awaitItem()).isInstanceOf(UiState.Loading::class.java)

            // Act
            viewModel.loadScene(sceneId)
            testDispatcher.scheduler.advanceUntilIdle()

            val successState = awaitItem() as UiState.Success
            assertThat(successState.data.categories).isEmpty()
        }
    }

    @Test
    fun loadScene_loads_scene_soundscapes_successfully() = runTest {
        // Arrange
        val sceneSoundscape = SceneSoundscape(
            sceneId = sceneId,
            categoryId = categoryId,
            categoryName = categoryName,
            displayOrder = 0,
            mixVolume = 0.8f,
            intensityLevel = IntensityLevel.II
        )
        val tracks = listOf(
            SoundscapeTrack(1L, categoryId, "Track 1", "path1", IntensityLevel.I),
            SoundscapeTrack(2L, categoryId, "Track 2", "path2", IntensityLevel.II)
        )

        every { sceneSoundscapeRepository.observeByScene(sceneId) } returns flowOf(listOf(sceneSoundscape))
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(tracks)
        every { audioEngine.getPlayer(categoryId) } returns null

        // Act
        viewModel.loadScene(sceneId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = (viewModel.uiState.value as UiState.Success).data
        assertThat(state.categories).hasSize(1)
        assertThat(state.categories[0].sceneSoundscape.categoryName).isEqualTo(categoryName)
        assertThat(state.categories[0].availableIntensities).containsExactlyInAnyOrder(
            IntensityLevel.I,
            IntensityLevel.II
        )
    }

    @Test
    fun setMasterVolume_updates_audio_engine_and_state() = runTest {
        // Arrange
        val sceneSoundscape = SceneSoundscape(
            sceneId = sceneId,
            categoryId = categoryId,
            categoryName = categoryName,
            displayOrder = 0,
            mixVolume = 0.8f,
            intensityLevel = IntensityLevel.I
        )
        every { sceneSoundscapeRepository.observeByScene(sceneId) } returns flowOf(listOf(sceneSoundscape))
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        every { audioEngine.getPlayer(categoryId) } returns null

        viewModel.loadScene(sceneId)
        testDispatcher.scheduler.advanceUntilIdle()

        val newVolume = 0.5f

        // Act
        viewModel.setMasterVolume(newVolume)

        // Assert
        verify { audioEngine.setMasterVolume(newVolume) }
        val state = (viewModel.uiState.value as UiState.Success).data
        assertThat(state.masterVolume).isEqualTo(newVolume)
    }

    @Test
    fun playCategory_creates_player_and_starts_playback() = runTest {
        // Arrange
        val sceneSoundscape = SceneSoundscape(
            sceneId = sceneId,
            categoryId = categoryId,
            categoryName = categoryName,
            displayOrder = 0,
            mixVolume = 0.8f,
            intensityLevel = IntensityLevel.I
        )
        val tracks = listOf(
            SoundscapeTrack(1L, categoryId, "Track 1", "path1", IntensityLevel.I)
        )
        val mockPlayer: CategoryPlayer = mockk(relaxed = true)
        val isPlayingFlow = MutableStateFlow(false)

        every { sceneSoundscapeRepository.observeByScene(sceneId) } returns flowOf(listOf(sceneSoundscape))
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(tracks)
        every { audioEngine.getPlayer(categoryId) } returns null
        every { audioEngine.addCategory(categoryId) } returns mockPlayer
        every { mockPlayer.isPlaying } returns isPlayingFlow

        viewModel.loadScene(sceneId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.playCategory(categoryId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify { audioEngine.addCategory(categoryId) }
        verify { mockPlayer.setMixVolume(0.8f) }
        verify { mockPlayer.rollRandomTrack(tracks) }
    }

    @Test
    fun pauseCategory_pauses_the_player() = runTest {
        // Arrange
        val sceneSoundscape = SceneSoundscape(
            sceneId = sceneId,
            categoryId = categoryId,
            categoryName = categoryName,
            displayOrder = 0,
            mixVolume = 0.8f,
            intensityLevel = IntensityLevel.I
        )
        val mockPlayer: CategoryPlayer = mockk(relaxed = true)

        every { sceneSoundscapeRepository.observeByScene(sceneId) } returns flowOf(listOf(sceneSoundscape))
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        every { audioEngine.getPlayer(categoryId) } returns mockPlayer

        viewModel.loadScene(sceneId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.pauseCategory(categoryId)

        // Assert
        verify { mockPlayer.pause() }
    }

    @Test
    fun setIntensity_updates_repository_and_switches_track_if_playing() = runTest {
        // Arrange
        val sceneSoundscape = SceneSoundscape(
            sceneId = sceneId,
            categoryId = categoryId,
            categoryName = categoryName,
            displayOrder = 0,
            mixVolume = 0.8f,
            intensityLevel = IntensityLevel.I
        )
        val tracks = listOf(
            SoundscapeTrack(1L, categoryId, "Track I", "path1", IntensityLevel.I),
            SoundscapeTrack(2L, categoryId, "Track II", "path2", IntensityLevel.II)
        )
        val mockPlayer: CategoryPlayer = mockk(relaxed = true)
        val isPlayingFlow = MutableStateFlow(true)

        every { sceneSoundscapeRepository.observeByScene(sceneId) } returns flowOf(listOf(sceneSoundscape))
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(tracks)
        every { audioEngine.getPlayer(categoryId) } returns mockPlayer
        every { mockPlayer.isPlaying } returns isPlayingFlow
        coEvery { sceneSoundscapeRepository.updateIntensityLevel(sceneId, categoryId, any()) } just Runs

        viewModel.loadScene(sceneId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.setIntensity(categoryId, IntensityLevel.II)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sceneSoundscapeRepository.updateIntensityLevel(sceneId, categoryId, IntensityLevel.II) }
        verify { mockPlayer.rollRandomTrack(listOf(tracks[1])) }
    }

    @Test
    fun setMix_updates_repository_and_player_volume() = runTest {
        // Arrange
        val sceneSoundscape = SceneSoundscape(
            sceneId = sceneId,
            categoryId = categoryId,
            categoryName = categoryName,
            displayOrder = 0,
            mixVolume = 0.8f,
            intensityLevel = IntensityLevel.I
        )
        val mockPlayer: CategoryPlayer = mockk(relaxed = true)

        every { sceneSoundscapeRepository.observeByScene(sceneId) } returns flowOf(listOf(sceneSoundscape))
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        every { audioEngine.getPlayer(categoryId) } returns mockPlayer
        coEvery { sceneSoundscapeRepository.updateMixVolume(sceneId, categoryId, any()) } just Runs

        viewModel.loadScene(sceneId)
        testDispatcher.scheduler.advanceUntilIdle()

        val newVolume = 0.6f

        // Act
        viewModel.setMix(categoryId, newVolume)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sceneSoundscapeRepository.updateMixVolume(sceneId, categoryId, newVolume) }
        verify { mockPlayer.setMixVolume(newVolume) }
    }

    @Test
    fun removeCategory_stops_playback_and_removes_from_repository() = runTest {
        // Arrange
        val sceneSoundscape = SceneSoundscape(
            sceneId = sceneId,
            categoryId = categoryId,
            categoryName = categoryName,
            displayOrder = 0,
            mixVolume = 0.8f,
            intensityLevel = IntensityLevel.I
        )

        every { sceneSoundscapeRepository.observeByScene(sceneId) } returns flowOf(listOf(sceneSoundscape))
        every { soundscapeRepository.observeTracksByCategory(categoryId) } returns flowOf(emptyList())
        every { audioEngine.getPlayer(categoryId) } returns null
        coEvery { sceneSoundscapeRepository.remove(sceneId, categoryId) } just Runs

        viewModel.loadScene(sceneId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.removeCategory(categoryId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify { audioEngine.removeCategory(categoryId) }
        coVerify { sceneSoundscapeRepository.remove(sceneId, categoryId) }
    }

    @Test
    fun addCategory_adds_to_repository_with_next_display_order() = runTest {
        // Arrange
        val existingCategory = SceneSoundscape(
            sceneId = sceneId,
            categoryId = 100L,
            categoryName = "Forest",
            displayOrder = 0,
            mixVolume = 0.8f,
            intensityLevel = IntensityLevel.I
        )
        val newCategoryId = 200L

        every { sceneSoundscapeRepository.observeByScene(sceneId) } returns flowOf(listOf(existingCategory))
        every { soundscapeRepository.observeTracksByCategory(any()) } returns flowOf(emptyList())
        every { audioEngine.getPlayer(any()) } returns null
        coEvery { sceneSoundscapeRepository.add(any(), any(), any(), any(), any()) } just Runs

        viewModel.loadScene(sceneId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.addCategory(newCategoryId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify {
            sceneSoundscapeRepository.add(
                sceneId = sceneId,
                categoryId = newCategoryId,
                displayOrder = 1, // Next order after existing category
                mixVolume = 1.0f,
                intensityLevel = IntensityLevel.I
            )
        }
    }

    @Test
    fun onCleared_releases_all_audio_players() {
        // Act
        viewModel.onCleared()

        // Assert
        verify { audioEngine.releaseAll() }
    }
}
