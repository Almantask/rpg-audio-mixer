package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryPlayerTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun makeTrack(id: Long, intensity: IntensityLevel) = SoundscapeTrack(
        id = id,
        categoryId = 1L,
        name = "Track $id",
        filePath = "/audio/track$id.mp3",
        intensityLevel = intensity,
    )

    @Test
    fun `isPlaying returns false initially`() = runTest {
        // Arrange
        val trackFactory: (String) -> TrackPlayer = mockk(relaxed = true)

        // Act
        val player = CategoryPlayer(trackFactory)

        // Assert
        assertThat(player.isPlaying.value).isFalse()
    }

    @Test
    fun `play creates a track player and plays it`() = runTest {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val factory: (String) -> TrackPlayer = { trackPlayer }
        val player = CategoryPlayer(factory)

        // Act
        player.play("/audio/track.mp3")

        // Assert
        verify(exactly = 1) { trackPlayer.play() }
    }

    @Test
    fun `isPlaying returns true after play`() = runTest {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val player = CategoryPlayer { trackPlayer }

        // Act
        player.play("/audio/track.mp3")

        // Assert
        assertThat(player.isPlaying.value).isTrue()
    }

    @Test
    fun `stop stops the current track`() = runTest {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val player = CategoryPlayer { trackPlayer }
        player.play("/audio/track.mp3")

        // Act
        player.stop()

        // Assert
        verify(exactly = 1) { trackPlayer.stop() }
        assertThat(player.isPlaying.value).isFalse()
    }

    @Test
    fun `setMixVolume updates the current player volume`() = runTest {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val player = CategoryPlayer { trackPlayer }
        player.play("/audio/track.mp3")

        // Act
        player.setMixVolume(0.5f)

        // Assert
        verify(exactly = 1) { trackPlayer.setVolume(0.5f) }
    }

    @Test
    fun `rollRandomTrack picks from pool and plays`() = runTest {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val player = CategoryPlayer { trackPlayer }
        val pool = listOf(
            makeTrack(1L, IntensityLevel.I),
            makeTrack(2L, IntensityLevel.I),
        )

        // Act
        player.rollRandomTrack(pool)

        // Assert
        verify(exactly = 1) { trackPlayer.play() }
        assertThat(player.isPlaying.value).isTrue()
    }

    @Test
    fun `rollRandomTrack with empty pool does not play`() = runTest {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val player = CategoryPlayer { trackPlayer }

        // Act
        player.rollRandomTrack(emptyList())

        // Assert
        verify(exactly = 0) { trackPlayer.play() }
        assertThat(player.isPlaying.value).isFalse()
    }
}
