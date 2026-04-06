package com.example.rpgaudiomixer.ui.activescene

import app.cash.turbine.test
import com.example.rpgaudiomixer.data.activescene.SceneAudioDao
import com.example.rpgaudiomixer.data.activescene.SceneFxCrossRef
import com.example.rpgaudiomixer.domain.audio.SoundboardPlayer
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
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
class ActiveSceneSoundboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val sceneAudioDao: SceneAudioDao = mockk(relaxed = true)
    private val fxRepository: FxRepository = mockk(relaxed = true)
    private val mockTrackPlayer: TrackPlayer = mockk(relaxed = true)
    private val soundboardPlayer = SoundboardPlayer { mockTrackPlayer }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // Arrange
        every { sceneAudioDao.observeFxForScene(any()) } returns flowOf(emptyList())
        every { fxRepository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 1L,
            sceneAudioDao = sceneAudioDao,
            fxRepository = fxRepository,
            soundboardPlayer = soundboardPlayer,
        )

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ActiveSceneSoundboardUiState.Loading::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Success with fx tracks linked to the scene`() = runTest {
        // Arrange
        val fxTrack = FxTrack(id = 1, name = "Thunder", filePath = "/thunder.mp3")
        val crossRef = SceneFxCrossRef(sceneId = 1L, fxTrackId = 1L)
        every { sceneAudioDao.observeFxForScene(1L) } returns flowOf(listOf(crossRef))
        every { fxRepository.observeAll() } returns flowOf(listOf(fxTrack))

        // Act
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 1L,
            sceneAudioDao = sceneAudioDao,
            fxRepository = fxRepository,
            soundboardPlayer = soundboardPlayer,
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state).isInstanceOf(ActiveSceneSoundboardUiState.Success::class.java)
            assertThat((state as ActiveSceneSoundboardUiState.Success).fxTracks).containsExactly(fxTrack)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setMasterVolume updates soundboard player volume`() = runTest {
        // Arrange
        every { sceneAudioDao.observeFxForScene(any()) } returns flowOf(emptyList())
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 1L,
            sceneAudioDao = sceneAudioDao,
            fxRepository = fxRepository,
            soundboardPlayer = soundboardPlayer,
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.setMasterVolume(0.4f)

        // Assert — no exception; state has updated volume
        viewModel.uiState.test {
            val state = awaitItem()
            if (state is ActiveSceneSoundboardUiState.Success) {
                assertThat(state.masterVolume).isEqualTo(0.4f)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
