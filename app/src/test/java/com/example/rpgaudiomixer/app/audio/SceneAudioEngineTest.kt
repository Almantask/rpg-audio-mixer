package com.example.rpgaudiomixer.app.audio

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SceneAudioEngineTest {

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

    private val rainPool = CategoryTrackPool(
        categoryId = 2L,
        categoryName = "Rain",
        tracksByIntensity = mapOf(
            1 to listOf("rain_light.mp3"),
            2 to listOf("rain_medium.mp3"),
        ),
    )

    private lateinit var engine: SceneAudioEngine

    @BeforeEach
    fun setUp() {
        engine = SceneAudioEngine(
            playerFactory = playerFactory,
            scope = testScope,
        )
    }

    // ---- loadScene() ----

    @Test
    fun `loadScene prepares category players for each pool`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer(any()) } returns player

        // Act
        engine.loadScene(listOf(forestPool, rainPool))
        engine.playCategorySound(forestPool.categoryId)

        // Assert — the player was created from the forest pool
        verify(exactly = 1) { playerFactory.createLoopingPlayer("forest_light.mp3") }
    }

    @Test
    fun `loadScene releases previous scene before loading new one`() {
        // Arrange
        val oldPlayer: AudioPlayerControl = mockk(relaxed = true)
        val newPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returnsMany listOf(oldPlayer, newPlayer)
        every { playerFactory.createLoopingPlayer("rain_light.mp3") } returns mockk(relaxed = true)
        engine.loadScene(listOf(forestPool))
        engine.playCategorySound(forestPool.categoryId)

        // Act — load a new scene
        engine.loadScene(listOf(rainPool))

        // Assert — old player was cleaned up
        verify { oldPlayer.stop() }
        verify { oldPlayer.release() }
    }

    // ---- playCategorySound() ----

    @Test
    fun `playCategorySound starts the correct category`() {
        // Arrange
        val forestPlayer: AudioPlayerControl = mockk(relaxed = true)
        val rainPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns forestPlayer
        every { playerFactory.createLoopingPlayer("rain_light.mp3") } returns rainPlayer
        engine.loadScene(listOf(forestPool, rainPool))

        // Act
        engine.playCategorySound(forestPool.categoryId)

        // Assert
        verify(exactly = 1) { forestPlayer.play() }
        verify(exactly = 0) { rainPlayer.play() }
    }

    @Test
    fun `playCategorySound with unknown categoryId does nothing`() {
        // Arrange
        engine.loadScene(listOf(forestPool))

        // Act & Assert — no exception
        engine.playCategorySound(999L)
    }

    // ---- pauseCategory() ----

    @Test
    fun `pauseCategory pauses the correct category`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns player
        engine.loadScene(listOf(forestPool))
        engine.playCategorySound(forestPool.categoryId)

        // Act
        engine.pauseCategory(forestPool.categoryId)

        // Assert
        verify { player.pause() }
    }

    // ---- setCategoryIntensity() ----

    @Test
    fun `setCategoryIntensity delegates to the correct category player`() = testScope.runTest {
        // Arrange
        val oldPlayer: AudioPlayerControl = mockk(relaxed = true)
        val newPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns oldPlayer
        every { playerFactory.createLoopingPlayer("forest_medium.mp3") } returns newPlayer
        engine.loadScene(listOf(forestPool))
        engine.playCategorySound(forestPool.categoryId)

        // Act
        engine.setCategoryIntensity(forestPool.categoryId, 2)

        // Assert — new player created for intensity 2
        verify(exactly = 1) { playerFactory.createLoopingPlayer("forest_medium.mp3") }
    }

    // ---- setCategoryMix() ----

    @Test
    fun `setCategoryMix updates the volume on the correct category`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns player
        engine.loadScene(listOf(forestPool))
        engine.playCategorySound(forestPool.categoryId)

        // Act
        engine.setCategoryMix(forestPool.categoryId, 0.5f)

        // Assert — volume = masterAtmosphereVolume(1.0) * cubicVolume(0.5) = 0.125
        verify { player.setVolume(0.125f) }
    }

    // ---- setMasterAtmosphereVolume() ----

    @Test
    fun `setMasterAtmosphereVolume propagates to all category players`() {
        // Arrange
        val forestPlayer: AudioPlayerControl = mockk(relaxed = true)
        val rainPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns forestPlayer
        every { playerFactory.createLoopingPlayer("rain_light.mp3") } returns rainPlayer
        engine.loadScene(listOf(forestPool, rainPool))
        engine.playCategorySound(forestPool.categoryId)
        engine.playCategorySound(rainPool.categoryId)

        // Act
        engine.setMasterAtmosphereVolume(0.5f)

        // Assert — both players receive updated volume: 0.5 * cubicVolume(1.0) = 0.5
        verify { forestPlayer.setVolume(0.5f) }
        verify { rainPlayer.setVolume(0.5f) }
    }

    // ---- playFx() ----

    @Test
    fun `playFx creates a one-shot player and plays it`() {
        // Arrange
        val fxPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createOneShotPlayer("sword_swing.mp3") } returns fxPlayer

        // Act
        engine.playFx("sword_swing.mp3")

        // Assert
        verify(exactly = 1) { playerFactory.createOneShotPlayer("sword_swing.mp3") }
        verify(exactly = 1) { fxPlayer.play() }
    }

    @Test
    fun `playFx applies master FX volume using cubic gain`() {
        // Arrange
        val fxPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createOneShotPlayer("sword_swing.mp3") } returns fxPlayer
        engine.setMasterFxVolume(0.5f)

        // Act
        engine.playFx("sword_swing.mp3")

        // Assert — volume = cubicVolume(0.5) = 0.125
        verify {
            fxPlayer.setVolume(
                withArg { assertThat(it).isCloseTo(0.125f, Offset.offset(0.001f)) },
            )
        }
    }

    @Test
    fun `setMasterFxVolume stores value for subsequent FX plays`() {
        // Arrange
        val fx1: AudioPlayerControl = mockk(relaxed = true)
        val fx2: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createOneShotPlayer("fx1.mp3") } returns fx1
        every { playerFactory.createOneShotPlayer("fx2.mp3") } returns fx2

        // Act
        engine.setMasterFxVolume(0.8f)
        engine.playFx("fx1.mp3")
        engine.setMasterFxVolume(0.3f)
        engine.playFx("fx2.mp3")

        // Assert
        verify { fx1.setVolume(withArg { assertThat(it).isCloseTo(0.512f, Offset.offset(0.001f)) }) }
        verify { fx2.setVolume(withArg { assertThat(it).isCloseTo(0.027f, Offset.offset(0.001f)) }) }
    }

    // ---- stopAll() ----

    @Test
    fun `stopAll stops all category players and releases FX players`() {
        // Arrange
        val forestPlayer: AudioPlayerControl = mockk(relaxed = true)
        val fxPlayer: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns forestPlayer
        every { playerFactory.createOneShotPlayer("boom.mp3") } returns fxPlayer
        engine.loadScene(listOf(forestPool))
        engine.playCategorySound(forestPool.categoryId)
        engine.playFx("boom.mp3")

        // Act
        engine.stopAll()

        // Assert
        verify { forestPlayer.stop() }
        verify { forestPlayer.release() }
        verify { fxPlayer.stop() }
        verify { fxPlayer.release() }
    }

    // ---- release() ----

    @Test
    fun `release cleans up all players and clears the scene`() {
        // Arrange
        val player: AudioPlayerControl = mockk(relaxed = true)
        every { playerFactory.createLoopingPlayer("forest_light.mp3") } returns player
        engine.loadScene(listOf(forestPool))
        engine.playCategorySound(forestPool.categoryId)

        // Act
        engine.release()

        // Assert
        verify { player.stop() }
        verify { player.release() }
        // Trying to play after release should do nothing (no crash)
        engine.playCategorySound(forestPool.categoryId)
        verify(exactly = 1) { playerFactory.createLoopingPlayer(any()) } // no new player created
    }

    @Test
    fun `release on empty engine does not throw`() {
        // Act & Assert — no exception
        engine.release()
    }
}
