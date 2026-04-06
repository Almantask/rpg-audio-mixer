package com.example.rpgaudiomixer.domain.audio

import com.example.rpgaudiomixer.domain.media.TrackPlayer
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneAudioEngineTest {

    private fun makeFactory(): (String) -> TrackPlayer {
        val player: TrackPlayer = mockk(relaxed = true)
        return { player }
    }

    @Test
    fun `addCategory adds a new CategoryPlayer`() {
        // Arrange
        val engine = SceneAudioEngine(makeFactory())

        // Act
        engine.addCategory(categoryId = 1L)

        // Assert
        assertThat(engine.getPlayer(1L)).isNotNull
    }

    @Test
    fun `removeCategory stops and removes the player`() {
        // Arrange
        val engine = SceneAudioEngine(makeFactory())
        engine.addCategory(categoryId = 1L)

        // Act
        engine.removeCategory(categoryId = 1L)

        // Assert
        assertThat(engine.getPlayer(1L)).isNull()
    }

    @Test
    fun `setMasterVolume updates all players mix volumes`() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true)
        val trackPlayer2: TrackPlayer = mockk(relaxed = true)
        var callCount = 0
        val factory: (String) -> TrackPlayer = {
            if (callCount++ == 0) trackPlayer1 else trackPlayer2
        }
        val engine = SceneAudioEngine(factory)
        engine.addCategory(1L)
        engine.addCategory(2L)
        engine.getPlayer(1L)?.play("/a.mp3")
        engine.getPlayer(2L)?.play("/b.mp3")

        // Act
        engine.setMasterVolume(0.5f)

        // Assert
        verify { trackPlayer1.setVolume(0.5f) }
        verify { trackPlayer2.setVolume(0.5f) }
    }

    @Test
    fun `releaseAll releases all players`() {
        // Arrange
        val engine = SceneAudioEngine(makeFactory())
        engine.addCategory(1L)
        engine.addCategory(2L)

        // Act
        engine.releaseAll()

        // Assert
        assertThat(engine.getPlayer(1L)).isNull()
        assertThat(engine.getPlayer(2L)).isNull()
    }
}
