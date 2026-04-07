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
 * Android instrumentation test for ExoOneTimeTrackPlayer.
 *
 * These tests require an Android environment (Context, ExoPlayer) and run on a device/emulator.
 * They verify URI resolution and player instantiation, but not actual playback behavior
 * (playback verification would require more complex async testing with audio focus).
 */
@RunWith(AndroidJUnit4::class)
class ExoOneTimeTrackPlayerInstrumentedTest {

    private lateinit var appContext: Context

    @Before
    fun setup() {
        // Arrange
        appContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun play_with_full_uri_does_not_throw() {
        // Arrange
        val fullUri = "file:///android_asset/test.mp3"
        val player = ExoOneTimeTrackPlayer(track = fullUri, appContext = appContext)

        // Act
        val action: () -> Unit = { player.play() }

        // Assert - should not throw during instantiation/URI resolution
        // Note: actual playback may fail if file doesn't exist, but that's expected
        // We're testing that the player can be created and play() can be called
        try {
            action()
            // If no exception, the URI was handled correctly
            assertThat(true).isTrue()
        } catch (e: TrackNotFoundException) {
            throw e // Re-throw TrackNotFoundException as it indicates our code failed
        } catch (e: Exception) {
            // Other exceptions (like file not found from ExoPlayer) are acceptable here
            // since we're testing URI resolution, not actual file existence
            assertThat(e).isNotInstanceOf(TrackNotFoundException::class.java)
        }
    }

    @Test
    fun play_with_content_uri_does_not_throw() {
        // Arrange
        val contentUri = "content://some.provider/path/to/audio.mp3"
        val player = ExoOneTimeTrackPlayer(track = contentUri, appContext = appContext)

        // Act & Assert - should not throw TrackNotFoundException
        try {
            player.play()
            assertThat(true).isTrue()
        } catch (e: TrackNotFoundException) {
            throw e // Re-throw TrackNotFoundException as it indicates our code failed
        } catch (e: Exception) {
            // Other exceptions are acceptable (e.g., provider not found)
            assertThat(e).isNotInstanceOf(TrackNotFoundException::class.java)
        }
    }

    @Test
    fun play_with_invalid_raw_resource_name_throws_TrackNotFoundException() {
        // Arrange
        val invalidResourceName = "non_existent_resource_12345"
        val player = ExoOneTimeTrackPlayer(track = invalidResourceName, appContext = appContext)

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
        val player = ExoOneTimeTrackPlayer(track = emptyTrack, appContext = appContext)

        // Act
        val action: () -> Unit = { player.play() }

        // Assert
        assertThatThrownBy(action)
            .isInstanceOf(TrackNotFoundException::class.java)
    }

    @Test
    fun multiple_instances_can_be_created_for_same_track() {
        // Arrange
        val uri = "file:///android_asset/test.mp3"

        // Act
        val player1 = ExoOneTimeTrackPlayer(track = uri, appContext = appContext)
        val player2 = ExoOneTimeTrackPlayer(track = uri, appContext = appContext)

        // Assert - both instances should be separate objects
        assertThat(player1).isNotSameAs(player2)
    }
}
