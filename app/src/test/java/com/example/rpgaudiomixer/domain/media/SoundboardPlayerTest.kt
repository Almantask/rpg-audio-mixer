package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.FxTrack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoundboardPlayerTest {

    private val fxTrack = FxTrack(
        id = 1L,
        name = "Thunder Crack",
        filePath = "thunder",
        tags = emptyList(),
        durationMs = 1_000L,
        playCount = 0,
        isDemoContent = false,
    )

    @Test
    fun triggerFx_creates_a_new_player_for_each_retrigger_without_stopping_the_existing_one() {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val player = SoundboardPlayer(
            trackFactory = trackFactory,
            maxConcurrentEffects = 5,
        )

        // Act
        player.triggerFx(fxTrack)
        player.triggerFx(fxTrack)

        // Assert
        assertThat(trackFactory.oneTimePlayers).hasSize(2)
        assertThat(trackFactory.oneTimePlayers[0].stopCalls).isEqualTo(0)
        assertThat(trackFactory.oneTimePlayers[0].playCalls).isEqualTo(1)
        assertThat(trackFactory.oneTimePlayers[1].playCalls).isEqualTo(1)
    }

    @Test
    fun triggerFx_when_the_limit_is_exceeded_stops_the_oldest_effect_instance() {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val player = SoundboardPlayer(
            trackFactory = trackFactory,
            maxConcurrentEffects = 2,
        )
        player.triggerFx(fxTrack)
        player.triggerFx(fxTrack.copy(id = 2L, filePath = "wolf"))

        // Act
        player.triggerFx(fxTrack.copy(id = 3L, filePath = "door"))

        // Assert
        assertThat(trackFactory.oneTimePlayers[0].stopCalls).isEqualTo(1)
        assertThat(player.activeInstanceCount).isEqualTo(2)
    }

    @Test
    fun setMasterVolume_updates_all_active_effect_instances() {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val player = SoundboardPlayer(
            trackFactory = trackFactory,
            maxConcurrentEffects = 5,
        )
        player.triggerFx(fxTrack)
        player.triggerFx(fxTrack.copy(id = 2L, filePath = "wolf"))

        // Act
        player.setMasterVolume(0.5f)

        // Assert
        assertThat(trackFactory.oneTimePlayers[0].volumeHistory.last()).isEqualTo(0.5f)
        assertThat(trackFactory.oneTimePlayers[1].volumeHistory.last()).isEqualTo(0.5f)
    }

    @Test
    fun stopFx_stops_only_the_requested_instance() {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val player = SoundboardPlayer(
            trackFactory = trackFactory,
            maxConcurrentEffects = 5,
        )
        val firstId = player.triggerFx(fxTrack)
        val secondId = player.triggerFx(fxTrack.copy(id = 2L, filePath = "wolf"))

        // Act
        player.stopFx(firstId)

        // Assert
        assertThat(trackFactory.oneTimePlayers[0].stopCalls).isEqualTo(1)
        assertThat(trackFactory.oneTimePlayers[1].stopCalls).isEqualTo(0)
        assertThat(player.activeInstanceIds).isEqualTo(listOf(secondId))
    }
}
