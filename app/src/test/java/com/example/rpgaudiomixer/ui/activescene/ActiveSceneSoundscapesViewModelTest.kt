package com.example.rpgaudiomixer.ui.activescene

import app.cash.turbine.test
import com.example.rpgaudiomixer.data.activescene.SceneAudioDao
import com.example.rpgaudiomixer.data.activescene.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.domain.audio.CategoryPlayer
import com.example.rpgaudiomixer.domain.audio.SceneAudioEngine
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
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
    private val sceneAudioDao: SceneAudioDao = mockk(relaxed = true)
    private val soundscapeRepository: SoundscapeRepository = mockk(relaxed = true)

    private val mockTrackPlayer: TrackPlayer = mockk(relaxed = true)
    private val engine = SceneAudioEngine { mockTrackPlayer }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        engine.releaseAll()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange
        every { sceneAudioDao.observeSoundscapesForScene(any()) } returns flowOf(emptyList())

        // Act
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 1L,
            sceneAudioDao = sceneAudioDao,
            soundscapeRepository = soundscapeRepository,
            audioEngine = engine,
        )

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ActiveSceneSoundscapesUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setMasterVolume updates the engine master volume`() = runTest {
        // Arrange
        every { sceneAudioDao.observeSoundscapesForScene(any()) } returns flowOf(emptyList())
        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 1L,
            sceneAudioDao = sceneAudioDao,
            soundscapeRepository = soundscapeRepository,
            audioEngine = engine,
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.setMasterVolume(0.6f)

        // Assert
        assertThat(engine.masterVolume.value).isEqualTo(0.6f)
    }
}
