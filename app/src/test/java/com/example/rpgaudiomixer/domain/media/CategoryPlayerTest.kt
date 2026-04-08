package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CategoryPlayerTest {

    private val trackFactory = RecordingTrackFactory()

    @Test
    fun play_creates_a_loopable_player_for_the_requested_track_and_marks_it_playing() {
        // Arrange
        val categoryPlayer = CategoryPlayer(trackFactory = trackFactory)

        // Act
        categoryPlayer.play(trackPath = "rain_loop")

        // Assert
        assertThat(trackFactory.createdLoopablePlayers.single().track).isEqualTo("rain_loop")
        assertThat(trackFactory.createdLoopablePlayers.single().playCalls).isEqualTo(1)
        assertThat(categoryPlayer.isPlaying.value).isTrue()
    }

    @Test
    fun play_replaces_the_previous_loop_when_a_different_track_is_requested() {
        // Arrange
        val categoryPlayer = CategoryPlayer(trackFactory = trackFactory)
        categoryPlayer.play(trackPath = "rain_loop")
        val firstPlayer = trackFactory.createdLoopablePlayers.single()

        // Act
        categoryPlayer.play(trackPath = "wind_loop")

        // Assert
        assertThat(firstPlayer.stopCalls).isEqualTo(1)
        assertThat(firstPlayer.releaseCalls).isEqualTo(1)
        assertThat(trackFactory.createdLoopablePlayers.last().track).isEqualTo("wind_loop")
    }

    @Test
    fun rollRandomTrack_plays_the_selected_track_from_the_pool() {
        // Arrange
        val categoryPlayer = CategoryPlayer(
            trackFactory = trackFactory,
            randomIndexPicker = { 1 },
        )
        val pool = listOf(
            soundscapeTrack(id = 1L, filePath = "rain_loop"),
            soundscapeTrack(id = 2L, filePath = "wind_loop"),
        )

        // Act
        val selectedTrack = categoryPlayer.rollRandomTrack(pool)

        // Assert
        assertThat(selectedTrack).isEqualTo(pool[1])
        assertThat(trackFactory.createdLoopablePlayers.single().track).isEqualTo("wind_loop")
    }

    @Test
    fun rollRandomTrack_returns_null_when_the_pool_is_empty() {
        // Arrange
        val categoryPlayer = CategoryPlayer(trackFactory = trackFactory)

        // Act
        val selectedTrack = categoryPlayer.rollRandomTrack(emptyList())

        // Assert
        assertThat(selectedTrack).isNull()
        assertThat(trackFactory.createdLoopablePlayers).isEmpty()
    }

    @Test
    fun setMixVolume_updates_the_current_player_volume() {
        // Arrange
        val categoryPlayer = CategoryPlayer(trackFactory = trackFactory)
        categoryPlayer.play(trackPath = "rain_loop")
        val player = trackFactory.createdLoopablePlayers.single()

        // Act
        categoryPlayer.setMixVolume(0.35f)

        // Assert
        assertThat(player.latestVolume).isEqualTo(0.35f)
    }

    @Test
    fun pause_and_resume_update_the_playing_state() {
        // Arrange
        val categoryPlayer = CategoryPlayer(trackFactory = trackFactory)
        categoryPlayer.play(trackPath = "rain_loop")

        // Act
        categoryPlayer.pause()
        categoryPlayer.resume()

        // Assert
        assertThat(categoryPlayer.isPlaying.value).isTrue()
        assertThat(trackFactory.createdLoopablePlayers.single().pauseCalls).isEqualTo(1)
        assertThat(trackFactory.createdLoopablePlayers.single().resumeCalls).isEqualTo(1)
    }

    private fun soundscapeTrack(id: Long, filePath: String) = SoundscapeTrack(
        id = id,
        categoryId = 10L,
        name = filePath,
        filePath = filePath,
        intensityLevel = IntensityLevel.I,
        mixVolume = 1f,
    )
}
