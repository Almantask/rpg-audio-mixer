package com.example.rpgaudiomixer.infra.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LocalTrackRepositoryTest {

    private val repository = LocalTrackRepository()

    @Test
    fun getTrackFilePath_returns_android_asset_track_uri_for_filename() {
        // Arrange
        val trackName = "ambience_forest"

        // Act
        val result = repository.getTrackFilePath(trackName)

        // Assert
        assertThat(result).isEqualTo("file:///android_asset/tracks/ambience_forest.mp3")
    }

    @Test
    fun getCategoryFolderPath_returns_android_asset_folder_uri_with_trailing_slash() {
        // Arrange
        val category = "combat"

        // Act
        val result = repository.getCategoryFolderPath(category)

        // Assert
        assertThat(result).isEqualTo("file:///android_asset/tracks/combat/")
    }
}