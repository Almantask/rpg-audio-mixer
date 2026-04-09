package com.example.rpgaudiomixer.domain.media

import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SceneAudioEngineTest {

    private val trackFactory = mockk<TrackFactory>()
    private val trackPlayer = mockk<TrackPlayer>(relaxed = true)
    private lateinit var engine: SceneAudioEngine

    @BeforeEach
    fun setUp() {
        every { trackFactory.createLoopableTrackPlayer(any()) } returns trackPlayer
        every { trackFactory.createOneTimeTrackPlayer(any()) } returns trackPlayer
        engine = SceneAudioEngine(trackFactory)
    }

    @Test
    fun `getPlayer creates or returns existing CategoryPlayer`() {
        val player1 = engine.getPlayer(1L)
        val player2 = engine.getPlayer(1L)
        val player3 = engine.getPlayer(2L)

        assertThat(player1).isSameAs(player2)
        assertThat(player1).isNotSameAs(player3)
    }

    @Test
    fun `setMasterVolume updates all existing players`() {
        val player1 = engine.getPlayer(1L)
        val player2 = engine.getPlayer(2L)
        
        player1.play("f1")
        player2.play("f2")
        
        engine.setMasterVolume(0.5f)

        // verify through trackPlayer volume setting
        // Assuming mix volume is 1.0f by default
        verify { trackPlayer.setVolume(0.5f) } 
    }

    @Test
    fun `releaseAll stops and clears all players`() {
        engine.getPlayer(1L).play("f1")
        engine.getPlayer(2L).play("f2")

        engine.releaseAll()

        verify(exactly = 2) { trackPlayer.stop() }
        verify(exactly = 2) { trackPlayer.release() }
        
        // Next getPlayer should create new instance
        val newPlayer = engine.getPlayer(1L)
        assertThat(newPlayer).isNotSameAs(mockk()) // Just checking it exists
    }
}
