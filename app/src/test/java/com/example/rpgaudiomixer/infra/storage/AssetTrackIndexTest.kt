package com.example.rpgaudiomixer.infra.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AssetTrackIndexTest {

    @Test
    fun functional_interface_can_be_implemented_with_lambda() {
        // Arrange & Act
        val index = AssetTrackIndex { path ->
            path == "tracks/ambient.mp3"
        }

        // Assert
        assertThat(index.exists("tracks/ambient.mp3")).isTrue()
        assertThat(index.exists("tracks/combat.mp3")).isFalse()
    }

    @Test
    fun index_returning_false_for_non_existent_asset() {
        // Arrange
        val index = AssetTrackIndex { false }

        // Act
        val result = index.exists("any/path.mp3")

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun index_returning_true_for_existing_asset() {
        // Arrange
        val index = AssetTrackIndex { true }

        // Act
        val result = index.exists("tracks/music.mp3")

        // Assert
        assertThat(result).isTrue()
    }

    @Test
    fun index_can_check_multiple_paths() {
        // Arrange
        val existingPaths = setOf(
            "tracks/forest/ambient1.mp3",
            "tracks/forest/ambient2.mp3",
            "tracks/combat/battle.mp3"
        )
        val index = AssetTrackIndex { path -> path in existingPaths }

        // Act & Assert
        assertThat(index.exists("tracks/forest/ambient1.mp3")).isTrue()
        assertThat(index.exists("tracks/forest/ambient2.mp3")).isTrue()
        assertThat(index.exists("tracks/combat/battle.mp3")).isTrue()
        assertThat(index.exists("tracks/ocean/waves.mp3")).isFalse()
    }

    @Test
    fun index_handles_empty_string() {
        // Arrange
        val index = AssetTrackIndex { path -> path.isNotEmpty() }

        // Act
        val result = index.exists("")

        // Assert
        assertThat(result).isFalse()
    }

    @Test
    fun index_can_use_path_patterns() {
        // Arrange
        val index = AssetTrackIndex { path ->
            path.startsWith("tracks/") && path.endsWith(".mp3")
        }

        // Act & Assert
        assertThat(index.exists("tracks/sound.mp3")).isTrue()
        assertThat(index.exists("tracks/music/song.mp3")).isTrue()
        assertThat(index.exists("audio/sound.mp3")).isFalse()
        assertThat(index.exists("tracks/sound.wav")).isFalse()
    }

    @Test
    fun multiple_indices_can_coexist() {
        // Arrange
        val index1 = AssetTrackIndex { path -> path.contains("ambient") }
        val index2 = AssetTrackIndex { path -> path.contains("combat") }

        // Act
        val result1 = index1.exists("tracks/ambient.mp3")
        val result2 = index2.exists("tracks/ambient.mp3")

        // Assert
        assertThat(result1).isTrue()
        assertThat(result2).isFalse()
    }

    @Test
    fun index_can_handle_special_characters_in_path() {
        // Arrange
        val specialPaths = setOf(
            "tracks/café.mp3",
            "tracks/señor.mp3",
            "tracks/file with spaces.mp3"
        )
        val index = AssetTrackIndex { path -> path in specialPaths }

        // Act & Assert
        assertThat(index.exists("tracks/café.mp3")).isTrue()
        assertThat(index.exists("tracks/señor.mp3")).isTrue()
        assertThat(index.exists("tracks/file with spaces.mp3")).isTrue()
        assertThat(index.exists("tracks/normal.mp3")).isFalse()
    }
}
