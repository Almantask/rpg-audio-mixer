package com.example.rpgaudiomixer.domain.media

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneAudioEngineTest {

    @Test
    fun setMasterVolume_scales_each_category_by_its_saved_mix_volume() {
        // Arrange
        val firstCategoryPlayer = RecordingCategoryPlayer()
        val secondCategoryPlayer = RecordingCategoryPlayer()
        val sceneAudioEngine = SceneAudioEngine(
            categoryPlayerFactory = SequenceCategoryPlayerFactory(firstCategoryPlayer, secondCategoryPlayer),
        )
        sceneAudioEngine.addCategory(categoryId = 1L)
        sceneAudioEngine.addCategory(categoryId = 2L)
        sceneAudioEngine.setCategoryMixVolume(categoryId = 1L, mixVolume = 1f)
        sceneAudioEngine.setCategoryMixVolume(categoryId = 2L, mixVolume = 0.5f)

        // Act
        sceneAudioEngine.setMasterVolume(0.8f)

        // Assert
        assertThat(firstCategoryPlayer.latestMixVolume).isEqualTo(0.8f)
        assertThat(secondCategoryPlayer.latestMixVolume).isEqualTo(0.4f)
    }

    @Test
    fun setCategoryMixVolume_updates_only_the_target_category_output() {
        // Arrange
        val firstCategoryPlayer = RecordingCategoryPlayer()
        val secondCategoryPlayer = RecordingCategoryPlayer()
        val sceneAudioEngine = SceneAudioEngine(
            categoryPlayerFactory = SequenceCategoryPlayerFactory(firstCategoryPlayer, secondCategoryPlayer),
        )
        sceneAudioEngine.addCategory(categoryId = 1L)
        sceneAudioEngine.addCategory(categoryId = 2L)
        sceneAudioEngine.setMasterVolume(0.5f)

        // Act
        sceneAudioEngine.setCategoryMixVolume(categoryId = 1L, mixVolume = 0.3f)

        // Assert
        assertThat(firstCategoryPlayer.latestMixVolume).isEqualTo(0.15f)
        assertThat(secondCategoryPlayer.latestMixVolume).isEqualTo(0.5f)
    }

    @Test
    fun playCategory_creates_a_category_on_demand_and_delegates_playback() {
        // Arrange
        val categoryPlayer = RecordingCategoryPlayer()
        val sceneAudioEngine = SceneAudioEngine(
            categoryPlayerFactory = SequenceCategoryPlayerFactory(categoryPlayer),
        )

        // Act
        sceneAudioEngine.play(categoryId = 42L, trackPath = "rain_loop")

        // Assert
        assertThat(categoryPlayer.playedTracks).containsExactly("rain_loop")
        assertThat(categoryPlayer.latestMixVolume).isEqualTo(1f)
    }

    @Test
    fun removeCategory_releases_the_player_and_forgets_its_mix_state() {
        // Arrange
        val categoryPlayer = RecordingCategoryPlayer()
        val replacementPlayer = RecordingCategoryPlayer()
        val sceneAudioEngine = SceneAudioEngine(
            categoryPlayerFactory = SequenceCategoryPlayerFactory(categoryPlayer, replacementPlayer),
        )
        sceneAudioEngine.addCategory(categoryId = 42L)
        sceneAudioEngine.setCategoryMixVolume(categoryId = 42L, mixVolume = 0.2f)
        sceneAudioEngine.removeCategory(categoryId = 42L)

        // Act
        sceneAudioEngine.play(categoryId = 42L, trackPath = "wind_loop")

        // Assert
        assertThat(categoryPlayer.releaseCalls).isEqualTo(1)
        assertThat(replacementPlayer.latestMixVolume).isEqualTo(1f)
    }

    @Test
    fun releaseAll_releases_every_category_player() {
        // Arrange
        val firstCategoryPlayer = RecordingCategoryPlayer()
        val secondCategoryPlayer = RecordingCategoryPlayer()
        val sceneAudioEngine = SceneAudioEngine(
            categoryPlayerFactory = SequenceCategoryPlayerFactory(firstCategoryPlayer, secondCategoryPlayer),
        )
        sceneAudioEngine.addCategory(categoryId = 1L)
        sceneAudioEngine.addCategory(categoryId = 2L)

        // Act
        sceneAudioEngine.releaseAll()

        // Assert
        assertThat(firstCategoryPlayer.releaseCalls).isEqualTo(1)
        assertThat(secondCategoryPlayer.releaseCalls).isEqualTo(1)
    }
}
