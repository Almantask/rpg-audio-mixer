package com.example.rpgaudiomixer.ui.activescene

import com.example.rpgaudiomixer.data.activescene.SceneAudioDao
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveSceneSoundboardPlayCountTest {

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
    fun `triggerFx increments play count in repository`() = runTest {
        // Arrange
        every { sceneAudioDao.observeFxForScene(any()) } returns flowOf(emptyList())
        every { fxRepository.observeAll() } returns flowOf(emptyList())
        val fxTrack = FxTrack(id = 7L, name = "Sword Clash", filePath = "/sword.mp3")
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 1L,
            sceneAudioDao = sceneAudioDao,
            fxRepository = fxRepository,
            soundboardPlayer = soundboardPlayer,
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.triggerFxWithStats(fxTrack)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { fxRepository.incrementPlayCount(7L) }
    }
}
