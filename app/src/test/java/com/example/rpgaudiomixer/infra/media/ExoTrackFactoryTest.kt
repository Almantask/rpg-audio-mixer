package com.example.rpgaudiomixer.infra.media

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExoTrackFactoryTest {

    private val factory = ExoTrackFactory()

    private val trackId = "a-track-id"

    @Test
    fun createLoopableTrackPlayer_returns_an_ExoLoopableTrackPlayer() {
        // Act
        val player = factory.createLoopableTrackPlayer(trackId)

        // Assert
        assertThat(player).isInstanceOf(ExoLoopableTrackPlayer::class.java)
    }

    @Test
    fun createOneTimeTrackPlayer_returns_an_ExoOneTimeTrackPlayer() {
        // Act
        val player = factory.createOneTimeTrackPlayer(trackId)

        // Assert
        assertThat(player).isInstanceOf(ExoOneTimeTrackPlayer::class.java)
    }
}