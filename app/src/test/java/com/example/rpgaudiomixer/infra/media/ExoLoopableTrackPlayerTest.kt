package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExoLoopableTrackPlayerTest {

    private val mockResources: Resources = mockk()
    private val mockContext: Context = mockk {
        every { resources } returns mockResources
        every { packageName } returns "com.example.rpgaudiomixer"
    }
    private val mockPlayer: ExoPlayer = mockk(relaxed = true)
    private val mockUri: Uri = mockk()

    @BeforeEach
    fun setUp() {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockUri
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    private fun buildSut(track: String) = ExoLoopableTrackPlayer(
        track = track,
        appContext = mockContext,
        playerProvider = { mockPlayer },
    )

    @Test
    fun `play with full URI track creates player and sets media`() {
        // Arrange
        val sut = buildSut("file:///android_asset/audio/battle.mp3")

        // Act
        sut.play()

        // Assert
        verify { mockPlayer.setMediaItem(any()) }
        verify { mockPlayer.prepare() }
    }

    @Test
    fun `play with raw resource name resolves via getIdentifier and creates player`() {
        // Arrange
        every { mockResources.getIdentifier("dog_bark", "raw", "com.example.rpgaudiomixer") } returns 999
        val sut = buildSut("dog_bark")

        // Act
        sut.play()

        // Assert
        verify { mockPlayer.setMediaItem(any()) }
        verify { mockPlayer.prepare() }
    }

    @Test
    fun `play with unknown track name throws TrackNotFoundException`() {
        // Arrange
        every { mockResources.getIdentifier("ghost_track", "raw", "com.example.rpgaudiomixer") } returns 0
        val sut = buildSut("ghost_track")

        // Act & Assert
        assertThatThrownBy { sut.play() }
            .isInstanceOf(TrackNotFoundException::class.java)
            .hasMessageContaining("ghost_track")
    }

    @Test
    fun `play sets repeatMode to REPEAT_MODE_ONE`() {
        // Arrange
        val sut = buildSut("file:///android_asset/audio/loop.mp3")

        // Act
        sut.play()

        // Assert
        verify { mockPlayer.repeatMode = Player.REPEAT_MODE_ONE }
    }

    @Test
    fun `play sets playWhenReady to true`() {
        // Arrange
        val sut = buildSut("file:///android_asset/audio/loop.mp3")

        // Act
        sut.play()

        // Assert
        verify { mockPlayer.playWhenReady = true }
    }
}
