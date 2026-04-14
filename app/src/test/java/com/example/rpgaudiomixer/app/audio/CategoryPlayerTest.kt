package com.example.rpgaudiomixer.app.audio

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryPlayerTest {

    private val playerFactory: AudioPlayerFactory = mockk()
    private val testScope = TestScope()

    private val forestPool = CategoryTrackPool(
        categoryId = 1L,
        categoryName = "Forest",
        tracksByIntensity = mapOf(
            1 to listOf("forest_light.mp3"),
            2 to listOf("forest_medium.mp3"),
            3 to listOf("forest_heavy.mp3"),
        ),
    )

    private val emptyPool = CategoryTrackPool(
        categoryId = 2L,
        categoryName = "Empty",
        tracksByIntensity = emptyMap(),
    )

    private fun createCategoryPlayer(
        trackPool: CategoryTrackPool = forestPool,
        crossfadeDurationMs: Long = 2000L,
        crossfadeSteps: Int = 10,
    ): CategoryPlayer = CategoryPlayer(
        trackPool = trackPool,
        playerFactory = playerFactory,
        scope = testScope,
        crossfadeDurationMs = crossfadeDurationMs,
        crossfadeSteps = crossfadeSteps,
        random = Random(42),
    )

    // ---- play() ----

    @Test
    fun `play creates a looping player from the default intensity and starts playback`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns player
        val categoryPlayer = createCategoryPlayer()

        // Act
        categoryPlayer.play()

        // Assert
        verify(exactly = 1) { playerFactory.createLoopingPlayer("forest_light.mp3") }
        verify(exactly = 1) { player.play() }
    }

    @Test
    fun `play sets initial volume using cubic gain formula`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer(any()) } returns player
        val categoryPlayer = createCategoryPlayer()
        // Default: masterVolume=1.0, mix=1.0  → volume = 1.0 * cubicVolume(1.0) = 1.0

        // Act
        categoryPlayer.play()

        // Assert
        verify { player.setVolume(1.0f) }
    }

    @Test
    fun `play marks the player as playing`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer(any()) } returns player
        val categoryPlayer = createCategoryPlayer()

        // Act
        categoryPlayer.play()

        // Assert
        assertThat(categoryPlayer.isPlaying).isTrue()
    }

    @Test
    fun `play with empty track pool does nothing`() {
        // Arrange
        val categoryPlayer = createCategoryPlayer(trackPool = emptyPool)

        // Act
        categoryPlayer.play()

        // Assert
        assertThat(categoryPlayer.isPlaying).isFalse()
        verify(exactly = 0) { playerFactory.createLoopingPlayer(any()) }
    }

    @Test
    fun `play stops previous player before creating a new one`() {
        // Arrange
        val player1: AudioPlayerControl = mockk(relaxed = true)
        val player2: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returnsMany listOf(player1, player2)
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.play()

        // Assert
        verify(exactly = 1) { player1.stop() }
        verify(exactly = 1) { player1.release() }
        verify(exactly = 1) { player2.play() }
    }

    // ---- stop() ----

    @Test
    fun `stop stops and releases the active player`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer(any()) } returns player
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.stop()

        // Assert
        verify { player.stop() }
        verify { player.release() }
        assertThat(categoryPlayer.isPlaying).isFalse()
    }

    @Test
    fun `stop on a non-playing player does not throw`() {
        // Arrange
        val categoryPlayer = createCategoryPlayer()

        // Act & Assert — no exception
        categoryPlayer.stop()
        assertThat(categoryPlayer.isPlaying).isFalse()
    }

    // ---- pause() ----

    @Test
    fun `pause pauses the active player`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer(any()) } returns player
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.pause()

        // Assert
        verify { player.pause() }
        assertThat(categoryPlayer.isPlaying).isFalse()
    }

    // ---- setIntensity() ----

    @Test
    fun `setIntensity while not playing only updates the intensity level`() {
        // Arrange
        val categoryPlayer = createCategoryPlayer()

        // Act
        categoryPlayer.setIntensity(2)

        // Assert
        verify(exactly = 0) { playerFactory.createLoopingPlayer(any()) }
    }

    @Test
    fun `setIntensity to the same level does not trigger crossfade`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns player
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.setIntensity(1) // already at intensity 1

        // Assert — only the initial play should have created a player
        verify(exactly = 1) { playerFactory.createLoopingPlayer(any()) }
    }

    @Test
    fun `setIntensity while playing triggers crossfade — new player is created and plays`() = testScope.runTest {
        // Arrange
        val oldPlayer: AudioPlayerControl = mockk(relaxed = true)
        val newPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns oldPlayer
        every { playerFactory.createLoopingPlayer("forest_medium.mp3") } returns newPlayer
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.setIntensity(2)
        runCurrent()

        // Assert
        verify(exactly = 1) { playerFactory.createLoopingPlayer("forest_medium.mp3") }
        verify(exactly = 1) { newPlayer.play() }
    }

    @Test
    fun `setIntensity crossfade starts new player at zero volume`() = testScope.runTest {
        // Arrange
        val oldPlayer: AudioPlayerControl = mockk(relaxed = true)
        val newPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns oldPlayer
        every { playerFactory.createLoopingPlayer("forest_medium.mp3") } returns newPlayer
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.setIntensity(2)
        runCurrent()

        // Assert — new player should start at volume 0 before crossfade ramps up
        verify { newPlayer.setVolume(0f) }
    }

    @Test
    fun `setIntensity crossfade completes — old player is stopped and released`() = testScope.runTest {
        // Arrange
        val oldPlayer: AudioPlayerControl = mockk(relaxed = true)
        val newPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns oldPlayer
        every { playerFactory.createLoopingPlayer("forest_medium.mp3") } returns newPlayer
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.setIntensity(2)
        advanceTimeBy(2100L) // past the 2000ms crossfade duration
        runCurrent()

        // Assert
        verify(exactly = 1) { oldPlayer.stop() }
        verify(exactly = 1) { oldPlayer.release() }
    }

    @Test
    fun `setIntensity crossfade completes — new player reaches target volume`() = testScope.runTest {
        // Arrange
        val oldPlayer: AudioPlayerControl = mockk(relaxed = true)
        val newPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns oldPlayer
        every { playerFactory.createLoopingPlayer("forest_medium.mp3") } returns newPlayer
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.setIntensity(2)
        advanceTimeBy(2100L)
        runCurrent()

        // Assert — final step sets full target volume (1.0 * cubicVolume(1.0) * 1.0 = 1.0)
        verify { newPlayer.setVolume(1.0f) }
    }

    @Test
    fun `setIntensity with invalid level throws`() {
        // Arrange
        val categoryPlayer = createCategoryPlayer()

        // Act & Assert
        assertThatThrownBy { categoryPlayer.setIntensity(0) }
            .isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { categoryPlayer.setIntensity(4) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `rapid intensity changes cancel previous crossfade`() = testScope.runTest {
        // Arrange
        val player1: AudioPlayerControl = mockk(relaxed = true)
        val player2: AudioPlayerControl = mockk(relaxed = true)
        val player3: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns player1
        every { playerFactory.createLoopingPlayer("forest_medium.mp3") } returns player2
        every { playerFactory.createLoopingPlayer("forest_heavy.mp3") } returns player3
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act — change intensity twice rapidly
        categoryPlayer.setIntensity(2)
        advanceTimeBy(500L) // partially through first crossfade
        categoryPlayer.setIntensity(3)
        advanceTimeBy(2100L) // complete second crossfade
        runCurrent()

        // Assert — player2 (intermediate) should be cleaned up
        verify { player2.stop() }
        verify { player2.release() }
        // player3 should be the final active player
        verify(exactly = 1) { player3.play() }
    }

    // ---- setVolume() / setMasterVolume() ----

    @Test
    fun `setVolume updates the active player volume`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer(any()) } returns player
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.setVolume(0.5f)

        // Assert — volume = masterVolume(1.0) * cubicVolume(0.5) = 1.0 * 0.125 = 0.125
        verify { player.setVolume(0.125f) }
    }

    @Test
    fun `setMasterVolume updates the active player volume`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer(any()) } returns player
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.setMasterVolume(0.5f)

        // Assert — volume = masterVolume(0.5) * cubicVolume(1.0) = 0.5 * 1.0 = 0.5
        verify { player.setVolume(0.5f) }
    }

    @Test
    fun `volume formula combines master and cubic mix`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer(any()) } returns player
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.setMasterVolume(0.8f)
        categoryPlayer.setVolume(0.5f)

        // Act
        categoryPlayer.play()

        // Assert — volume = 0.8 * cubicVolume(0.5) = 0.8 * 0.125 = 0.1
        verify { player.setVolume(withArg { assertThat(it).isCloseTo(0.1f, Offset.offset(0.001f)) }) }
    }

    @Test
    fun `setVolume with invalid value throws`() {
        // Arrange
        val categoryPlayer = createCategoryPlayer()

        // Act & Assert
        assertThatThrownBy { categoryPlayer.setVolume(-0.1f) }
            .isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy { categoryPlayer.setVolume(1.1f) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    // ---- release() ----

    @Test
    fun `release stops and releases all players`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer(any()) } returns player
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()

        // Act
        categoryPlayer.release()

        // Assert
        verify { player.stop() }
        verify { player.release() }
        assertThat(categoryPlayer.isPlaying).isFalse()
    }

    // ---- pause cancels crossfade ----

    @Test
    fun `pause during crossfade cancels the crossfade and cleans up fading-out player`() = testScope.runTest {
        // Arrange
        val oldPlayer: AudioPlayerControl = mockk(relaxed = true)
        val newPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns oldPlayer
        every { playerFactory.createLoopingPlayer("forest_medium.mp3") } returns newPlayer
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.play()
        categoryPlayer.setIntensity(2)
        advanceTimeBy(500L) // mid-crossfade

        // Act
        categoryPlayer.pause()
        runCurrent()

        // Assert — fading-out player should be cleaned up
        verify { oldPlayer.stop() }
        verify { oldPlayer.release() }
        // active player should be paused
        verify { newPlayer.pause() }
    }

    // ---- track selection after intensity change (non-playing) ----

    @Test
    fun `play after setIntensity uses the new intensity pool`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_heavy.mp3") } returns player
        val categoryPlayer = createCategoryPlayer()
        categoryPlayer.setIntensity(3)

        // Act
        categoryPlayer.play()

        // Assert
        verify(exactly = 1) { playerFactory.createLoopingPlayer("forest_heavy.mp3") }
    }
}
