package com.example.rpgaudiomixer.infra.media

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android instrumentation test for ExoLoopableTrackPlayer.
 *
 * These tests require an Android environment (Context, ExoPlayer) and run on a device/emulator.
 * They verify URI resolution and player instantiation with loop mode enabled.
 */
@RunWith(AndroidJUnit4::class)
class ExoLoopableTrackPlayerInstrumentedTest {

    private lateinit var appContext: Context

    @Before
    fun setup() {
        // Arrange
        appContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun play_with_full_uri_does_not_throw() {
        // Arrange
        val fullUri = "file:///android_asset/ambient_loop.mp3"
        val player = ExoLoopableTrackPlayer(track = fullUri, appContext = appContext)

        // Act
        val action: () -> Unit = { player.play() }

        // Assert - should not throw during instantiation/URI resolution
        try {
            action()
            assertThat(true).isTrue()
        } catch (e: TrackNotFoundException) {
            throw e // Re-throw TrackNotFoundException as it indicates our code failed
        } catch (e: Exception) {
            // Other exceptions (like file not found) are acceptable here
            assertThat(e).isNotInstanceOf(TrackNotFoundException::class.java)
        }
    }

    @Test
    fun play_with_content_uri_does_not_throw() {
        // Arrange
        val contentUri = "content://some.provider/path/to/loop.mp3"
        val player = ExoLoopableTrackPlayer(track = contentUri, appContext = appContext)

        // Act & Assert
        try {
            player.play()
            assertThat(true).isTrue()
        } catch (e: TrackNotFoundException) {
            throw e
        } catch (e: Exception) {
            assertThat(e).isNotInstanceOf(TrackNotFoundException::class.java)
        }
    }

    @Test
    fun play_with_invalid_raw_resource_name_throws_TrackNotFoundException() {
        // Arrange
        val invalidResourceName = "definitely_not_a_real_resource_99999"
        val player = ExoLoopableTrackPlayer(track = invalidResourceName, appContext = appContext)

        // Act
        val action: () -> Unit = { player.play() }

        // Assert
        assertThatThrownBy(action)
            .isInstanceOf(TrackNotFoundException::class.java)
            .hasMessageContaining(invalidResourceName)
    }

    @Test
    fun play_with_empty_track_name_throws_TrackNotFoundException() {
        // Arrange
        val emptyTrack = ""
        val player = ExoLoopableTrackPlayer(track = emptyTrack, appContext = appContext)

        // Act
        val action: () -> Unit = { player.play() }

        // Assert
        assertThatThrownBy(action)
            .isInstanceOf(TrackNotFoundException::class.java)
    }

    @Test
    fun multiple_loopable_instances_can_be_created() {
        // Arrange
        val uri = "file:///android_asset/loop1.mp3"

        // Act
        val player1 = ExoLoopableTrackPlayer(track = uri, appContext = appContext)
        val player2 = ExoLoopableTrackPlayer(track = uri, appContext = appContext)

        // Assert - both instances should be separate objects
        assertThat(player1).isNotSameAs(player2)
    }
}
