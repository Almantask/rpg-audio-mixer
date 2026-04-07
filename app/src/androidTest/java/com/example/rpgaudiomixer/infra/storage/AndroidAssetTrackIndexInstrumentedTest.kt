package com.example.rpgaudiomixer.infra.storage

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android instrumentation test for AndroidAssetTrackIndex.
 *
 * Tests the actual Android AssetManager interaction.
 */
@RunWith(AndroidJUnit4::class)
class AndroidAssetTrackIndexInstrumentedTest {

    private lateinit var appContext: Context
    private lateinit var assetIndex: AndroidAssetTrackIndex

    @Before
    fun setup() {
        // Arrange
        appContext = ApplicationProvider.getApplicationContext()
        assetIndex = AndroidAssetTrackIndex(appContext.assets)
    }

    @Test
    fun exists_with_non_existent_asset_returns_false() {
        // Act
        val result = assetIndex.exists("definitely/does/not/exist.mp3")

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun exists_with_empty_path_returns_false() {
        // Act
        val result = assetIndex.exists("")

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun exists_with_invalid_path_returns_false() {
        // Arrange
        val invalidPaths = listOf(
            "///invalid/path.mp3",
            "../escaping/path.mp3",
            "path/with spaces/file.mp3",
            "missing_extension"
        )

        // Act & Assert
        invalidPaths.forEach { path ->
            val result = assetIndex.exists(path)
            assertThat(result).isFalse()
        }
    }

    @Test
    fun exists_called_twice_with_same_path_returns_consistent_result() {
        // Arrange
        val testPath = "test/asset/path.mp3"

        // Act
        val result1 = assetIndex.exists(testPath)
        val result2 = assetIndex.exists(testPath)

        // Assert - should be consistent regardless of result
        assertThat(result1).isEqualTo(result2)
    }

    @Test
    fun exists_with_directory_path_returns_false() {
        // Arrange - directories cannot be opened like files
        val directoryPaths = listOf("tracks/", "tracks", "audio/ambient/")

        // Act & Assert
        directoryPaths.forEach { path ->
            val result = assetIndex.exists(path)
            // Directories should return false when trying to open as file
            assertThat(result).isFalse()
        }
    }

    @Test
    fun exists_handles_exception_gracefully() {
        // Arrange - paths that might cause various exceptions
        val problematicPaths = listOf(
            "null\u0000byte/path.mp3",
            String(ByteArray(10000) { 'a'.code.toByte() }), // very long path
            "unicode/emoji/🎵.mp3"
        )

        // Act & Assert - should not crash, just return false
        problematicPaths.forEach { path ->
            val result = assetIndex.exists(path)
            assertThat(result).isFalse()
        }
    }
}
