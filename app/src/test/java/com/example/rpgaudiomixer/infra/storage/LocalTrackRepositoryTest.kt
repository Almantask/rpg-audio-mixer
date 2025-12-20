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
}