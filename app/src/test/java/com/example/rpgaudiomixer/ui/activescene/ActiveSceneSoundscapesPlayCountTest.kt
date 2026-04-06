package com.example.rpgaudiomixer.ui.activescene

import com.example.rpgaudiomixer.data.activescene.SceneAudioDao
import com.example.rpgaudiomixer.domain.audio.SceneAudioEngine
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
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
import com.example.rpgaudiomixer.data.soundscape.SoundscapeTrackDao

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveSceneSoundscapesPlayCountTest {

    private val testDispatcher = StandardTestDispatcher()
    private val sceneAudioDao: SceneAudioDao = mockk(relaxed = true)
    private val soundscapeRepository: SoundscapeRepository = mockk(relaxed = true)
    private val soundscapeTrackDao: SoundscapeTrackDao = mockk(relaxed = true)
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
    fun `playCategoryWithStats increments soundscape track play count`() = runTest {
        // Arrange
        every { sceneAudioDao.observeSoundscapesForScene(any()) } returns flowOf(emptyList())
        every { soundscapeRepository.observeAllCategories() } returns flowOf(emptyList())

        val viewModel = ActiveSceneSoundscapesViewModel(
            sceneId = 1L,
            sceneAudioDao = sceneAudioDao,
            soundscapeRepository = soundscapeRepository,
            audioEngine = engine,
            soundscapeTrackDao = soundscapeTrackDao,
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.playCategoryWithStats(categoryId = 99L, trackId = 42L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { soundscapeTrackDao.incrementPlayCount(42L) }
    }
}
