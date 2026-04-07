package com.example.rpgaudiomixer.domain.media

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CategoryPlayerTest {

    private val trackFactory: TrackFactory = mockk(relaxed = true)
    private var masterVolume = 1.0f
    private val categoryPlayer = CategoryPlayerImpl(trackFactory) { masterVolume }

    private val track1 = "forest-ambience"
    private val track2 = "rain-loop"

    @Test
    fun playTrack_creates_loopable_track_player_and_starts_playback() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer

        // Act
        categoryPlayer.playTrack(track1)

        // Assert
        verify(exactly = 1) { trackFactory.createLoopableTrackPlayer(track1) }
        verify(exactly = 1) { trackPlayer.playTrack() }
        assertThat(categoryPlayer.isPlaying.value).isTrue()
    }

    @Test
    fun playTrack_stops_previous_track_before_playing_new_one() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer1
        every { trackFactory.createLoopableTrackPlayer(track2) } returns trackPlayer2

        // Act
        categoryPlayer.playTrack(track1)
        categoryPlayer.playTrack(track2)

        // Assert
        verify(exactly = 1) { trackPlayer1.stopTrack() }
        verify(exactly = 1) { trackPlayer1.release() }
        verify(exactly = 1) { trackPlayer2.playTrack() }
    }

    @Test
    fun pauseTrack_pauses_current_player() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer

        // Act
        categoryPlayer.playTrack(track1)
        categoryPlayer.pauseTrack()

        // Assert
        verify(exactly = 1) { trackPlayer.pauseTrack() }
        assertThat(categoryPlayer.isPlaying.value).isFalse()
    }

    @Test
    fun resumeTrack_resumes_current_player() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer

        // Act
        categoryPlayer.playTrack(track1)
        categoryPlayer.pauseTrack()
        categoryPlayer.resumeTrack()

        // Assert
        verify(exactly = 1) { trackPlayer.resumeTrack() }
        assertThat(categoryPlayer.isPlaying.value).isTrue()
    }

    @Test
    fun stopTrack_stops_and_releases_current_player() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer

        // Act
        categoryPlayer.playTrack(track1)
        categoryPlayer.stopTrack()

        // Assert
        verify(exactly = 1) { trackPlayer.stopTrack() }
        verify(exactly = 1) { trackPlayer.release() }
        assertThat(categoryPlayer.isPlaying.value).isFalse()
    }

    @Test
    fun rollRandomTrack_plays_a_track_from_the_pool() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(any()) } returns trackPlayer
        val trackPool = listOf("track1", "track2", "track3")

        // Act
        categoryPlayer.rollRandomTrack(trackPool)

        // Assert
        verify(exactly = 1) { trackFactory.createLoopableTrackPlayer(match { it in trackPool }) }
        verify(exactly = 1) { trackPlayer.playTrack() }
    }

    @Test
    fun rollRandomTrack_with_empty_pool_does_nothing() {
        // Arrange
        val emptyPool = emptyList<String>()

        // Act
        categoryPlayer.rollRandomTrack(emptyPool)

        // Assert
        verify(exactly = 0) { trackFactory.createLoopableTrackPlayer(any()) }
    }

    @Test
    fun setMixVolume_updates_player_volume_with_master_multiplied() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer
        masterVolume = 0.8f

        // Act
        categoryPlayer.playTrack(track1)
        categoryPlayer.setMixVolume(0.5f)

        // Assert
        // Volume should be set twice: once on play (mix=1.0 * master=0.8 = 0.8)
        // and once on setMixVolume (mix=0.5 * master=0.8 = 0.4)
        verify(atLeast = 1) { trackPlayer.setVolume(any()) }
        verify { trackPlayer.setVolume(0.4f) }
    }

    @Test
    fun setMixVolume_coerces_value_between_0_and_1() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer
        masterVolume = 1.0f

        // Act
        categoryPlayer.playTrack(track1)
        categoryPlayer.setMixVolume(1.5f) // Should be coerced to 1.0

        // Assert
        verify { trackPlayer.setVolume(1.0f) }
    }

    @Test
    fun setMixVolume_below_zero_is_coerced_to_zero() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer
        masterVolume = 1.0f

        // Act
        categoryPlayer.playTrack(track1)
        categoryPlayer.setMixVolume(-0.5f) // Should be coerced to 0.0

        // Assert
        verify { trackPlayer.setVolume(0.0f) }
    }

    @Test
    fun release_stops_and_releases_current_player() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer

        // Act
        categoryPlayer.playTrack(track1)
        categoryPlayer.release()

        // Assert
        verify(exactly = 1) { trackPlayer.stopTrack() }
        verify(exactly = 1) { trackPlayer.release() }
    }

    @Test
    fun master_volume_change_affects_effective_volume() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(track1) } returns trackPlayer
        masterVolume = 1.0f

        // Act
        categoryPlayer.playTrack(track1)
        categoryPlayer.setMixVolume(0.5f)

        // Change master volume
        masterVolume = 0.6f
        categoryPlayer.setMixVolume(0.5f) // Re-set to trigger recalculation

        // Assert
        // Effective volume should be 0.5 * 0.6 = 0.3
        verify { trackPlayer.setVolume(0.3f) }
    }
}
