package com.example.rpgaudiomixer.infra.storage

import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class LocalTrackRepositoryTest {

    @Test
    fun getTrackFilePath_given_track_exists_in_raw_returns_raw_resource_name() {
        // Arrange
        val trackName = "dog_bark"
        val repository = LocalTrackRepository(
            rawResourceResolver = { name -> if (name == trackName) 123 else null },
            assetTrackIndex = { false },
        )

        // Act
        val result = repository.getTrackFilePath(trackName)

        // Assert
        assertThat(result).isEqualTo(trackName)
    }

    @Test
    fun getTrackFilePath_given_track_not_in_raw_but_exists_in_assets_returns_android_asset_uri() {
        // Arrange
        val trackName = "ambience_forest"
        val assetPath = "tracks/$trackName.mp3"
        val repository = LocalTrackRepository(
            rawResourceResolver = { null },
            assetTrackIndex = { path -> path == assetPath },
        )

        // Act
        val result = repository.getTrackFilePath(trackName)

        // Assert
        assertThat(result).isEqualTo("file:///android_asset/$assetPath")
    }

    @Test
    fun getTrackFilePath_given_track_in_neither_raw_nor_assets_throws() {
        // Arrange
        val trackName = "non_existing"
        val repository = LocalTrackRepository(
            rawResourceResolver = { null },
            assetTrackIndex = { false },
        )

        // Act
        val call: () -> Unit = { repository.getTrackFilePath(trackName) }

        // Assert
        assertThatThrownBy(call)
            .isInstanceOf(TrackNotFoundException::class.java)
            .hasMessageContaining(trackName)
    }

    @Test
    fun getCategoryFolderPath_returns_android_asset_folder_uri_with_trailing_slash() {
        // Arrange
        val category = "combat"
        val repository = LocalTrackRepository(
            rawResourceResolver = { null },
            assetTrackIndex = { false },
        )

        // Act
        val result = repository.getCategoryFolderPath(category)

        // Assert
        assertThat(result).isEqualTo("file:///android_asset/tracks/combat/")
    }

    @Test
    fun getTrackFilePath_given_multiple_tracks_only_returns_matching_track_from_raw() {
        // Arrange
        val trackName1 = "thunder"
        val trackName2 = "rain"
        val repository = LocalTrackRepository(
            rawResourceResolver = { name ->
                when (name) {
                    trackName1 -> 456
                    trackName2 -> 789
                    else -> null
                }
            },
            assetTrackIndex = { false },
        )

        // Act
        val result1 = repository.getTrackFilePath(trackName1)
        val result2 = repository.getTrackFilePath(trackName2)

        // Assert
        assertThat(result1).isEqualTo(trackName1)
        assertThat(result2).isEqualTo(trackName2)
    }

    @Test
    fun getTrackFilePath_prefers_raw_resource_over_assets_when_both_exist() {
        // Arrange
        val trackName = "shared_track"
        val assetPath = "tracks/$trackName.mp3"
        val repository = LocalTrackRepository(
            rawResourceResolver = { name -> if (name == trackName) 999 else null },
            assetTrackIndex = { path -> path == assetPath },
        )

        // Act
        val result = repository.getTrackFilePath(trackName)

        // Assert
        assertThat(result).isEqualTo(trackName)
    }

    @Test
    fun getCategoryFolderPath_handles_various_category_names() {
        // Arrange
        val repository = LocalTrackRepository(
            rawResourceResolver = { null },
            assetTrackIndex = { false },
        )

        // Act & Assert
        assertThat(repository.getCategoryFolderPath("forest"))
            .isEqualTo("file:///android_asset/tracks/forest/")
        assertThat(repository.getCategoryFolderPath("dungeon"))
            .isEqualTo("file:///android_asset/tracks/dungeon/")
        assertThat(repository.getCategoryFolderPath("tavern"))
            .isEqualTo("file:///android_asset/tracks/tavern/")
    }

    @Test
    fun getCategoryFolderPath_handles_empty_string() {
        // Arrange
        val repository = LocalTrackRepository(
            rawResourceResolver = { null },
            assetTrackIndex = { false },
        )

        // Act
        val result = repository.getCategoryFolderPath("")

        // Assert
        assertThat(result).isEqualTo("file:///android_asset/tracks//")
    }

    @Test
    fun getTrackFilePath_exception_message_includes_track_name() {
        // Arrange
        val trackName = "missing_sound_effect"
        val repository = LocalTrackRepository(
            rawResourceResolver = { null },
            assetTrackIndex = { false },
        )

        // Act & Assert
        assertThatThrownBy { repository.getTrackFilePath(trackName) }
            .isInstanceOf(TrackNotFoundException::class.java)
            .hasMessageContaining(trackName)
            .hasMessageContaining("res/raw")
            .hasMessageContaining("assets")
    }
}