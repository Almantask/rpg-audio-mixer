package com.example.rpgaudiomixer.infra.media

import org.junit.jupiter.api.Test

class ExoOneTimeTrackPlayerTest {
    private val existingTrack: String = "existing"
    private val nonExistingTrack: String = "non_existing"

    @Test
    fun play_given_track_exists_plays_it() {
        // Arrange
        val player = ExoLoopableTrackPlayer(existingTrack)

        // Act
        player.play()

        // Assert
        // track is played without exceptions
    }

    @Test
    fun play_given_track_does_not_exist_throws_trackNotFoundException() {
        // Arrange
        val player = ExoLoopableTrackPlayer(nonExistingTrack)

        // Act
        player.play()

        // Assert
    }
}