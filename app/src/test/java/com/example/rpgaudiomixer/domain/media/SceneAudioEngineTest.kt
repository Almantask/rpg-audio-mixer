package com.example.rpgaudiomixer.domain.media

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest

class SceneAudioEngineTest {

    @Test
    fun setMasterVolume_updates_all_category_volumes_proportionally() {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val engine = SceneAudioEngine(
            trackFactory = trackFactory,
            maxConcurrentCategories = 10,
        )
        engine.addCategory(1L).apply {
            setMixVolume(1f)
            play("weather")
        }
        engine.addCategory(2L).apply {
            setMixVolume(0.5f)
            play("interior")
        }

        // Act
        engine.setMasterVolume(0.8f)

        // Assert
        assertThat(trackFactory.loopPlayers[0].volumeHistory.last()).isEqualTo(0.8f)
        assertThat(trackFactory.loopPlayers[1].volumeHistory.last()).isEqualTo(0.4f)
    }

    @Test
    fun playCategory_when_the_limit_is_exceeded_stops_the_oldest_playing_category() {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val engine = SceneAudioEngine(
            trackFactory = trackFactory,
            maxConcurrentCategories = 2,
        )
        engine.addCategory(1L)
        engine.addCategory(2L)
        engine.addCategory(3L)
        engine.playCategory(1L, "weather")
        engine.playCategory(2L, "interior")

        // Act
        engine.playCategory(3L, "forest")

        // Assert
        assertThat(trackFactory.createdLoopTracks).containsExactly("weather", "interior", "forest")
        assertThat(trackFactory.loopPlayers[0].stopCalls).isEqualTo(1)
        assertThat(engine.getCategoryPlayer(1L)?.isPlaying?.value).isEqualTo(false)
        assertThat(engine.getCategoryPlayer(3L)?.isPlaying?.value).isEqualTo(true)
    }

    @Test
    fun startScene_fades_in_the_target_categories_and_tracks_the_active_scene() = runTest {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val engine = SceneAudioEngine(trackFactory = trackFactory)

        // Act
        engine.startScene(
            sceneId = 7L,
            categories = listOf(
                ScenePlaybackCategory(
                    categoryId = 11L,
                    trackPath = "rain",
                    targetMixVolume = 0.6f,
                ),
                ScenePlaybackCategory(
                    categoryId = 12L,
                    trackPath = "fireplace",
                    targetMixVolume = 0.3f,
                ),
            ),
            stepDelayMillis = 0L,
        )

        // Assert
        assertThat(engine.activeSceneId).isEqualTo(7L)
        assertThat(trackFactory.createdLoopTracks).containsExactly("rain", "fireplace")
        assertThat(trackFactory.loopPlayers[0].volumeHistory.last()).isEqualTo(0.6f)
        assertThat(trackFactory.loopPlayers[1].volumeHistory.last()).isEqualTo(0.3f)
    }

    @Test
    fun switchToScene_stops_the_previous_scene_categories_and_starts_the_new_scene() = runTest {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val engine = SceneAudioEngine(trackFactory = trackFactory)
        engine.startScene(
            sceneId = 7L,
            categories = listOf(
                ScenePlaybackCategory(
                    categoryId = 11L,
                    trackPath = "rain",
                    targetMixVolume = 0.6f,
                ),
            ),
            stepDelayMillis = 0L,
        )

        // Act
        engine.switchToScene(
            sceneId = 8L,
            categories = listOf(
                ScenePlaybackCategory(
                    categoryId = 12L,
                    trackPath = "forest",
                    targetMixVolume = 0.8f,
                ),
            ),
            stepDelayMillis = 0L,
        )

        // Assert
        assertThat(engine.activeSceneId).isEqualTo(8L)
        assertThat(trackFactory.loopPlayers[0].stopCalls).isEqualTo(1)
        assertThat(trackFactory.loopPlayers[1].volumeHistory.last()).isEqualTo(0.8f)
    }
}
