package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoundboardPlayerTest {

    private fun makeTrack(id: Long) = FxTrack(
        id = id,
        name = "FX $id",
        filePath = "/audio/fx$id.mp3",
    )

    @Test
    fun `triggerFx creates a new player and plays it`() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val player = SoundboardPlayer { trackPlayer }

        // Act
        player.triggerFx(makeTrack(1L))

        // Assert
        verify(exactly = 1) { trackPlayer.play() }
    }

    @Test
    fun `triggerFx twice for same track creates two separate players`() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true)
        val trackPlayer2: TrackPlayer = mockk(relaxed = true)
        var callCount = 0
        val factory: (String) -> TrackPlayer = {
            if (callCount++ == 0) trackPlayer1 else trackPlayer2
        }
        val player = SoundboardPlayer(factory)

        // Act
        player.triggerFx(makeTrack(1L))
        player.triggerFx(makeTrack(1L))

        // Assert
        verify(exactly = 1) { trackPlayer1.play() }
        verify(exactly = 1) { trackPlayer2.play() }
    }

    @Test
    fun `stopAll releases all active players`() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val player = SoundboardPlayer { trackPlayer }
        player.triggerFx(makeTrack(1L))
        player.triggerFx(makeTrack(2L))

        // Act
        player.stopAll()

        // Assert
        verify(exactly = 2) { trackPlayer.stop() }
    }

    @Test
    fun `setMasterVolume updates volume of all active players`() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val player = SoundboardPlayer { trackPlayer }
        player.triggerFx(makeTrack(1L))
        player.triggerFx(makeTrack(2L))

        // Act
        player.setMasterVolume(0.3f)

        // Assert
        verify(exactly = 2) { trackPlayer.setVolume(0.3f) }
    }

    @Test
    fun `activeFxCount returns number of active players`() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        val player = SoundboardPlayer { trackPlayer }

        // Act
        player.triggerFx(makeTrack(1L))
        player.triggerFx(makeTrack(2L))

        // Assert
        assertThat(player.activeFxCount).isEqualTo(2)
    }
}
