package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.library.SoundscapeTrack
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CategoryPlayerTest {

    private val trackFactory = mockk<TrackFactory>()
    private val trackPlayer = mockk<TrackPlayer>(relaxed = true)
    private lateinit var categoryPlayer: CategoryPlayer

    @BeforeEach
    fun setUp() {
        every { trackFactory.createLoopableTrackPlayer(any()) } returns trackPlayer
        categoryPlayer = CategoryPlayer(trackFactory)
    }

    @Test
    fun `play track creates and plays new player`() {
        categoryPlayer.play("track.mp3")

        verify { trackFactory.createLoopableTrackPlayer("track.mp3") }
        verify { trackPlayer.play() }
    }

    @Test
    fun `play same track as currently playing resumes instead of recreating`() {
        categoryPlayer.play("track.mp3")
        clearMocks(trackFactory, trackPlayer)

        categoryPlayer.play("track.mp3")

        verify(exactly = 0) { trackFactory.createLoopableTrackPlayer(any()) }
        verify { trackPlayer.resume() }
    }

    @Test
    fun `setMixVolume updates track player volume with master volume multiplication`() {
        categoryPlayer.setMasterVolume(0.5f)
        categoryPlayer.play("track.mp3")
        
        categoryPlayer.setMixVolume(0.8f)

        verify { trackPlayer.setVolume(0.4f) } // 0.5 * 0.8
    }

    @Test
    fun `rollRandomTrack selects track from pool and plays it`() {
        val track1 = SoundscapeTrack(name = "T1", filePath = "f1", intensityLevel = IntensityLevel.I, categoryId = 1L)
        val track2 = SoundscapeTrack(name = "T2", filePath = "f2", intensityLevel = IntensityLevel.I, categoryId = 1L)
        val pool = listOf(track1, track2)

        categoryPlayer.rollRandomTrack(pool)

        verify { trackPlayer.play() }
        assertThat(slot<String>().also { verify { trackFactory.createLoopableTrackPlayer(capture(it)) } }.captured)
            .isIn("f1", "f2")
    }

    @Test
    fun `isPlaying reflects playback state`() = runTest {
        categoryPlayer.play("track.mp3")
        assertThat(categoryPlayer.isPlaying.value).isTrue()

        categoryPlayer.pause()
        assertThat(categoryPlayer.isPlaying.value).isFalse()
    }
}
