package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SceneSoundscapeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
class ActiveSceneSoundscapesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val sceneRepository: SceneRepository = mockk(relaxed = true)
    private val sceneSoundscapeRepository: SceneSoundscapeRepository = mockk(relaxed = true)
    private val sceneAudioEngine: SceneAudioEngine = mockk(relaxed = true)
    private lateinit var viewModel: ActiveSceneSoundscapesViewModel

    private val sampleSceneId = 1L
    private val sampleScene = Scene(
        id = sampleSceneId,
        name = "Tavern",
        description = "A cozy tavern",
        tags = listOf("indoor", "social"),
        atmosphereVolumePercent = 75
    )

    private val sampleCategory1 = SoundscapeCategory(
        id = 1L,
        name = "Fire",
        iconResId = null,
        themeLabel = null
    )

    private val sampleCategory2 = SoundscapeCategory(
        id = 2L,
        name = "Crowd",
        iconResId = null,
        themeLabel = null
    )

    private val sampleSoundscape1 = SceneSoundscape(
        sceneId = sampleSceneId,
        category = sampleCategory1,
        intensityLevel = IntensityLevel.II,
        mixVolumePercent = 100,
        displayOrder = 0
    )

    private val sampleSoundscape2 = SceneSoundscape(
        sceneId = sampleSceneId,
        category = sampleCategory2,
        intensityLevel = IntensityLevel.I,
        mixVolumePercent = 80,
        displayOrder = 1
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
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(emptyList())

        // Act
        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(ActiveSceneSoundscapesUiState.Loading::class.java)
    }

    @Test
    fun `loadScene emits Success state with scene and soundscapes`() = runTest {
        // Arrange
        val soundscapes = listOf(sampleSoundscape1, sampleSoundscape2)
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(soundscapes)

        // Act
        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(ActiveSceneSoundscapesUiState.Success::class.java)
        val successState = state as ActiveSceneSoundscapesUiState.Success
        assertThat(successState.sceneName).isEqualTo("Tavern")
        assertThat(successState.atmosphereVolumePercent).isEqualTo(75)
        assertThat(successState.soundscapes).hasSize(2)
        assertThat(successState.soundscapes[0]).isEqualTo(sampleSoundscape1)
        assertThat(successState.soundscapes[1]).isEqualTo(sampleSoundscape2)
    }

    @Test
    fun `loadScene emits Success state with empty soundscapes list`() = runTest {
        // Arrange
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(emptyList())

        // Act
        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(ActiveSceneSoundscapesUiState.Success::class.java)
        assertThat((state as ActiveSceneSoundscapesUiState.Success).soundscapes).isEmpty()
    }

    @Test
    fun `loadScene emits Error state when scene not found`() = runTest {
        // Arrange
        coEvery { sceneRepository.getById(sampleSceneId) } returns null

        // Act
        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(ActiveSceneSoundscapesUiState.Error::class.java)
        assertThat((state as ActiveSceneSoundscapesUiState.Error).message).contains("not found")
    }

    @Test
    fun `loadScene emits Error state when repository throws exception`() = runTest {
        // Arrange
        val errorMessage = "Database error"
        coEvery { sceneRepository.getById(sampleSceneId) } throws RuntimeException(errorMessage)

        // Act
        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(ActiveSceneSoundscapesUiState.Error::class.java)
        assertThat((state as ActiveSceneSoundscapesUiState.Error).message).contains(errorMessage)
    }

    @Test
    fun `setMasterVolume updates atmosphereVolumePercent in UI state`() = runTest {
        // Arrange
        val soundscapes = listOf(sampleSoundscape1)
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(soundscapes)

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.setMasterVolume(50)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundscapesUiState.Success
        assertThat(state.atmosphereVolumePercent).isEqualTo(50)
        coVerify { sceneRepository.update(match { it.atmosphereVolumePercent == 50 }) }
        verify { sceneAudioEngine.setMasterVolume(0.5f) }
    }

    @Test
    fun `addSoundscape adds category to scene`() = runTest {
        // Arrange
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(emptyList())

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.addSoundscape(categoryId = 3L, intensityLevel = IntensityLevel.II)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sceneSoundscapeRepository.addToScene(sampleSceneId, 3L, IntensityLevel.II, 100) }
    }

    @Test
    fun `updateIntensity updates intensity level for soundscape`() = runTest {
        // Arrange
        val soundscapes = listOf(sampleSoundscape1)
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(soundscapes)

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.updateIntensity(categoryId = 1L, intensityLevel = IntensityLevel.III)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sceneSoundscapeRepository.updateIntensity(sampleSceneId, 1L, IntensityLevel.III) }
    }

    @Test
    fun `updateMixVolume updates mix volume for soundscape`() = runTest {
        // Arrange
        val soundscapes = listOf(sampleSoundscape1)
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(soundscapes)

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.updateMixVolume(categoryId = 1L, mixVolumePercent = 60)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sceneSoundscapeRepository.updateMixVolume(sampleSceneId, 1L, 60) }
    }

    @Test
    fun `removeSoundscape removes category from scene`() = runTest {
        // Arrange
        val soundscapes = listOf(sampleSoundscape1, sampleSoundscape2)
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(soundscapes)

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.removeSoundscape(categoryId = 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sceneSoundscapeRepository.removeFromScene(sampleSceneId, 1L) }
        verify { sceneAudioEngine.removeCategory(1L) }
    }

    @Test
    fun `reorderSoundscapes updates display orders`() = runTest {
        // Arrange
        val soundscapes = listOf(sampleSoundscape1, sampleSoundscape2)
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(soundscapes)

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        val reorderedList = listOf(sampleSoundscape2, sampleSoundscape1)
        viewModel.reorderSoundscapes(reorderedList)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { sceneSoundscapeRepository.updateDisplayOrders(sampleSceneId, reorderedList) }
    }

    @Test
    fun `clearError transitions from Error to previous state`() = runTest {
        // Arrange
        coEvery { sceneRepository.getById(sampleSceneId) } returns null

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value).isInstanceOf(ActiveSceneSoundscapesUiState.Error::class.java)

        // Act
        viewModel.clearError()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isInstanceOf(ActiveSceneSoundscapesUiState.Loading::class.java)
    }

    @Test
    fun `playScene integrates soundscapes with audio engine`() = runTest {
        // Arrange
        val soundscapes = listOf(sampleSoundscape1, sampleSoundscape2)
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(soundscapes)

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.playScene()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify { sceneAudioEngine.setMasterVolume(0.75f) }
        verify { sceneAudioEngine.addCategory(1L, IntensityLevel.II, 1.0f) }
        verify { sceneAudioEngine.addCategory(2L, IntensityLevel.I, 0.8f) }
    }

    @Test
    fun `pauseScene stops audio playback`() = runTest {
        // Arrange
        val soundscapes = listOf(sampleSoundscape1)
        coEvery { sceneRepository.getById(sampleSceneId) } returns sampleScene
        every { sceneSoundscapeRepository.observeByScene(sampleSceneId) } returns flowOf(soundscapes)

        viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = sampleSceneId,
            sceneRepository = sceneRepository,
            sceneSoundscapeRepository = sceneSoundscapeRepository,
            sceneAudioEngine = sceneAudioEngine
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.pauseScene()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        verify { sceneAudioEngine.releaseAll() }
    }
}
