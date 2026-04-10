package com.example.rpgaudiomixer.domain.media

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CategoryPlayerTest {

    private val trackFactory = FakeTrackFactory()

    @Test
    fun play_stops_the_previous_loop_and_starts_the_new_track() {
        // Arrange
        val player = CategoryPlayer(trackFactory = trackFactory)

        // Act
        player.play("rain")
        val firstTrackPlayer = trackFactory.loopPlayers.last()
        player.play("storm")
        val secondTrackPlayer = trackFactory.loopPlayers.last()

        // Assert
        assertThat(trackFactory.createdLoopTracks).containsExactly("rain", "storm")
        assertThat(firstTrackPlayer.stopCalls).isEqualTo(1)
        assertThat(secondTrackPlayer.playCalls).isEqualTo(1)
        assertThat(player.isPlaying.value).isEqualTo(true)
    }

    @Test
    fun setMixVolume_scales_the_current_track_volume_with_master_volume() {
        // Arrange
        val player = CategoryPlayer(trackFactory = trackFactory)
        player.play("rain")
        player.setMasterVolume(0.8f)

        // Act
        player.setMixVolume(0.5f)

        // Assert
        assertThat(trackFactory.loopPlayers.last().volumeHistory.last()).isEqualTo(0.4f)
    }

    @Test
    fun rollRandomTrack_returns_a_failure_when_the_pool_is_empty() {
        // Arrange
        val player = CategoryPlayer(trackFactory = trackFactory)

        // Act
        val result = player.rollRandomTrack(emptyList())

        // Assert
        assertThat(result.exceptionOrNull()?.javaClass).isEqualTo(IllegalArgumentException::class.java)
    }

    @Test
    fun pause_and_resume_update_the_playing_state() {
        // Arrange
        val player = CategoryPlayer(trackFactory = trackFactory)
        player.play("rain")

        // Act
        player.pause()
        player.resume()

        // Assert
        val activeTrackPlayer = trackFactory.loopPlayers.last()
        assertThat(activeTrackPlayer.pauseCalls).isEqualTo(1)
        assertThat(activeTrackPlayer.resumeCalls).isEqualTo(1)
        assertThat(player.isPlaying.value).isEqualTo(true)
    }
}
