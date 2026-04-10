package com.example.rpgaudiomixer.domain.media

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
}
