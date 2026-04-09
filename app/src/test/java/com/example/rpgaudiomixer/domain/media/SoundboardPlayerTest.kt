package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.FxTrack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoundboardPlayerTest {

    private val trackFactory = RecordingTrackFactory()
    private val fxTrack = FxTrack(
        id = 7L,
        name = "Thunder Crack",
        filePath = "thunder_crack",
        tags = emptyList(),
        durationMs = 900L,
        playCount = 0,
        isDemo = false,
    )

    @Test
    fun triggerFx_creates_a_one_time_player_plays_it_and_returns_an_instance_id() {
        // Arrange
        val soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory)

        // Act
        val result = soundboardPlayer.triggerFx(fxTrack)

        // Assert
        assertThat(result.startedInstanceId).isEqualTo(1L)
        assertThat(result.evictedInstanceId).isNull()
        assertThat(trackFactory.createdOneTimePlayers.single().track).isEqualTo("thunder_crack")
        assertThat(trackFactory.createdOneTimePlayers.single().playCalls).isEqualTo(1)
    }

    @Test
    fun triggerFx_twice_creates_two_overlapping_instances_for_the_same_effect() {
        // Arrange
        val soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory)

        // Act
        val firstId = soundboardPlayer.triggerFx(fxTrack).startedInstanceId
        val secondId = soundboardPlayer.triggerFx(fxTrack).startedInstanceId

        // Assert
        assertThat(firstId).isNotEqualTo(secondId)
        assertThat(trackFactory.createdOneTimePlayers).hasSize(2)
        assertThat(soundboardPlayer.isTrackPlaying(trackId = fxTrack.id)).isTrue()
    }

    @Test
    fun setMasterVolume_updates_all_active_effect_instances() {
        // Arrange
        val soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory)
        soundboardPlayer.triggerFx(fxTrack)
        soundboardPlayer.triggerFx(fxTrack.copy(id = 8L, filePath = "door_creak"))

        // Act
        soundboardPlayer.setMasterVolume(0.5f)

        // Assert
        assertThat(trackFactory.createdOneTimePlayers.map { it.latestVolume }).containsExactly(0.5f, 0.5f)
    }

    @Test
    fun stopFx_stops_only_the_requested_instance() {
        // Arrange
        val soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory)
        val firstId = soundboardPlayer.triggerFx(fxTrack).startedInstanceId
        val secondId = soundboardPlayer.triggerFx(fxTrack).startedInstanceId
        val firstPlayer = trackFactory.createdOneTimePlayers[0]
        val secondPlayer = trackFactory.createdOneTimePlayers[1]

        // Act
        soundboardPlayer.stopFx(firstId)

        // Assert
        assertThat(firstId).isNotEqualTo(secondId)
        assertThat(firstPlayer.stopCalls).isEqualTo(1)
        assertThat(firstPlayer.releaseCalls).isEqualTo(1)
        assertThat(secondPlayer.stopCalls).isEqualTo(0)
    }

    @Test
    fun releaseAll_stops_and_releases_every_active_instance() {
        // Arrange
        val soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory)
        soundboardPlayer.triggerFx(fxTrack)
        soundboardPlayer.triggerFx(fxTrack.copy(id = 8L, filePath = "door_creak"))

        // Act
        soundboardPlayer.releaseAll()

        // Assert
        assertThat(trackFactory.createdOneTimePlayers.map { it.releaseCalls }).containsExactly(1, 1)
        assertThat(soundboardPlayer.isTrackPlaying(trackId = fxTrack.id)).isFalse()
    }

    @Test
    fun triggerFx_when_the_concurrency_limit_is_exceeded_stops_the_oldest_instance() {
        // Arrange
        val soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory, maxConcurrentInstances = 5)

        // Act
        val results = (1..6).map { soundboardPlayer.triggerFx(fxTrack) }

        // Assert
        assertThat(results.last().startedInstanceId).isEqualTo(6L)
        assertThat(results.last().evictedInstanceId).isEqualTo(1L)
        assertThat(trackFactory.createdOneTimePlayers.first().stopCalls).isEqualTo(1)
        assertThat(trackFactory.createdOneTimePlayers.first().releaseCalls).isEqualTo(1)
        assertThat(trackFactory.createdOneTimePlayers.drop(1).map { it.stopCalls }).containsExactly(0, 0, 0, 0, 0)
    }
}
