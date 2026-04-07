package com.example.rpgaudiomixer.domain.media

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoundboardPlayerTest {

    private val trackFactory: TrackFactory = mockk(relaxed = true)
    private val soundboardPlayer = SoundboardPlayerImpl(trackFactory)

    private val fxTrack1 = "thunder-crack"
    private val fxTrack2 = "door-creak"

    @Test
    fun triggerFx_creates_one_time_player_and_plays() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(fxTrack1) } returns trackPlayer

        // Act
        val instanceId = soundboardPlayer.triggerFx(fxTrack1)

        // Assert
        assertThat(instanceId).isNotEmpty()
        verify(exactly = 1) { trackFactory.createOneTimeTrackPlayer(fxTrack1) }
        verify(exactly = 1) { trackPlayer.playTrack() }
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(1)
    }

    @Test
    fun triggerFx_twice_creates_two_separate_instances() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(fxTrack1) } returnsMany listOf(trackPlayer1, trackPlayer2)

        // Act
        val instanceId1 = soundboardPlayer.triggerFx(fxTrack1)
        val instanceId2 = soundboardPlayer.triggerFx(fxTrack1)

        // Assert
        assertThat(instanceId1).isNotEqualTo(instanceId2)
        verify(exactly = 2) { trackFactory.createOneTimeTrackPlayer(fxTrack1) }
        verify(exactly = 1) { trackPlayer1.playTrack() }
        verify(exactly = 1) { trackPlayer2.playTrack() }
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(2)
    }

    @Test
    fun triggerFx_for_different_tracks_creates_separate_players() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(fxTrack1) } returns trackPlayer1
        every { trackFactory.createOneTimeTrackPlayer(fxTrack2) } returns trackPlayer2

        // Act
        soundboardPlayer.triggerFx(fxTrack1)
        soundboardPlayer.triggerFx(fxTrack2)

        // Assert
        verify(exactly = 1) { trackFactory.createOneTimeTrackPlayer(fxTrack1) }
        verify(exactly = 1) { trackFactory.createOneTimeTrackPlayer(fxTrack2) }
        verify(exactly = 1) { trackPlayer1.playTrack() }
        verify(exactly = 1) { trackPlayer2.playTrack() }
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(2)
    }

    @Test
    fun stopFx_stops_and_releases_specific_instance() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(fxTrack1) } returns trackPlayer

        val instanceId = soundboardPlayer.triggerFx(fxTrack1)

        // Act
        soundboardPlayer.stopFx(instanceId)

        // Assert
        verify(exactly = 1) { trackPlayer.stopTrack() }
        verify(exactly = 1) { trackPlayer.release() }
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(0)
    }

    @Test
    fun stopFx_only_stops_the_specified_instance() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(fxTrack1) } returnsMany listOf(trackPlayer1, trackPlayer2)

        val instanceId1 = soundboardPlayer.triggerFx(fxTrack1)
        val instanceId2 = soundboardPlayer.triggerFx(fxTrack1)

        // Act
        soundboardPlayer.stopFx(instanceId1)

        // Assert
        verify(exactly = 1) { trackPlayer1.stopTrack() }
        verify(exactly = 1) { trackPlayer1.release() }
        verify(exactly = 0) { trackPlayer2.stopTrack() }
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(1)
    }

    @Test
    fun stopAll_stops_all_active_instances() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer3: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(any()) } returnsMany listOf(trackPlayer1, trackPlayer2, trackPlayer3)

        soundboardPlayer.triggerFx(fxTrack1)
        soundboardPlayer.triggerFx(fxTrack1)
        soundboardPlayer.triggerFx(fxTrack2)

        // Act
        soundboardPlayer.stopAll()

        // Assert
        verify(exactly = 1) { trackPlayer1.stopTrack() }
        verify(exactly = 1) { trackPlayer1.release() }
        verify(exactly = 1) { trackPlayer2.stopTrack() }
        verify(exactly = 1) { trackPlayer2.release() }
        verify(exactly = 1) { trackPlayer3.stopTrack() }
        verify(exactly = 1) { trackPlayer3.release() }
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(0)
    }

    @Test
    fun setMasterVolume_updates_volume_state() {
        // Act
        soundboardPlayer.setMasterVolume(0.7f)

        // Assert
        assertThat(soundboardPlayer.masterVolume.value).isEqualTo(0.7f)
    }

    @Test
    fun setMasterVolume_coerces_value_between_0_and_1() {
        // Act
        soundboardPlayer.setMasterVolume(1.5f)

        // Assert
        assertThat(soundboardPlayer.masterVolume.value).isEqualTo(1.0f)
    }

    @Test
    fun setMasterVolume_below_zero_is_coerced_to_zero() {
        // Act
        soundboardPlayer.setMasterVolume(-0.5f)

        // Assert
        assertThat(soundboardPlayer.masterVolume.value).isEqualTo(0.0f)
    }

    @Test
    fun setMasterVolume_updates_all_active_players() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(any()) } returnsMany listOf(trackPlayer1, trackPlayer2)

        soundboardPlayer.triggerFx(fxTrack1)
        soundboardPlayer.triggerFx(fxTrack2)

        // Act
        soundboardPlayer.setMasterVolume(0.6f)

        // Assert
        verify { trackPlayer1.setVolume(0.6f) }
        verify { trackPlayer2.setVolume(0.6f) }
    }

    @Test
    fun triggerFx_applies_current_master_volume() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(fxTrack1) } returns trackPlayer

        // Act
        soundboardPlayer.setMasterVolume(0.5f)
        soundboardPlayer.triggerFx(fxTrack1)

        // Assert
        verify { trackPlayer.setVolume(0.5f) }
    }

    @Test
    fun releaseAll_stops_all_players() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(any()) } returnsMany listOf(trackPlayer1, trackPlayer2)

        soundboardPlayer.triggerFx(fxTrack1)
        soundboardPlayer.triggerFx(fxTrack2)

        // Act
        soundboardPlayer.releaseAll()

        // Assert
        verify(exactly = 1) { trackPlayer1.stopTrack() }
        verify(exactly = 1) { trackPlayer1.release() }
        verify(exactly = 1) { trackPlayer2.stopTrack() }
        verify(exactly = 1) { trackPlayer2.release() }
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(0)
    }

    @Test
    fun activeInstanceCount_reflects_number_of_active_players() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer3: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createOneTimeTrackPlayer(any()) } returnsMany listOf(trackPlayer1, trackPlayer2, trackPlayer3)

        // Assert initial state
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(0)

        // Act & Assert
        val id1 = soundboardPlayer.triggerFx(fxTrack1)
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(1)

        val id2 = soundboardPlayer.triggerFx(fxTrack1)
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(2)

        val id3 = soundboardPlayer.triggerFx(fxTrack2)
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(3)

        soundboardPlayer.stopFx(id2)
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(2)

        soundboardPlayer.stopAll()
        assertThat(soundboardPlayer.activeInstanceCount.value).isEqualTo(0)
    }
}
