package com.example.rpgaudiomixer.domain.media

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneAudioEngineTest {

    private val trackFactory: TrackFactory = mockk(relaxed = true)
    private val sceneAudioEngine = SceneAudioEngineImpl(trackFactory)

    private val category1 = "weather"
    private val category2 = "interior"
    private val track1 = "rain-loop"

    @Test
    fun getCategoryPlayer_creates_new_player_for_new_category() {
        // Act
        val player = sceneAudioEngine.getCategoryPlayer(category1)

        // Assert
        assertThat(player).isNotNull()
        assertThat(player).isInstanceOf(CategoryPlayer::class.java)
    }

    @Test
    fun getCategoryPlayer_returns_same_player_for_same_category() {
        // Act
        val player1 = sceneAudioEngine.getCategoryPlayer(category1)
        val player2 = sceneAudioEngine.getCategoryPlayer(category1)

        // Assert
        assertThat(player1).isSameAs(player2)
    }

    @Test
    fun getCategoryPlayer_returns_different_players_for_different_categories() {
        // Act
        val player1 = sceneAudioEngine.getCategoryPlayer(category1)
        val player2 = sceneAudioEngine.getCategoryPlayer(category2)

        // Assert
        assertThat(player1).isNotSameAs(player2)
    }

    @Test
    fun addCategory_creates_category_player() {
        // Act
        sceneAudioEngine.addCategory(category1)
        val player = sceneAudioEngine.getCategoryPlayer(category1)

        // Assert
        assertThat(player).isNotNull()
    }

    @Test
    fun removeCategory_releases_and_removes_player() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(any()) } returns trackPlayer

        val player = sceneAudioEngine.getCategoryPlayer(category1)
        player.playTrack(track1)

        // Act
        sceneAudioEngine.removeCategory(category1)

        // Assert
        verify(exactly = 1) { trackPlayer.stopTrack() }
        verify(exactly = 1) { trackPlayer.release() }

        // Getting the category again should create a new player
        val newPlayer = sceneAudioEngine.getCategoryPlayer(category1)
        assertThat(newPlayer).isNotSameAs(player)
    }

    @Test
    fun setMasterVolume_updates_master_volume_state() {
        // Act
        sceneAudioEngine.setMasterVolume(0.7f)

        // Assert
        assertThat(sceneAudioEngine.masterVolume.value).isEqualTo(0.7f)
    }

    @Test
    fun setMasterVolume_coerces_value_between_0_and_1() {
        // Act
        sceneAudioEngine.setMasterVolume(1.5f)

        // Assert
        assertThat(sceneAudioEngine.masterVolume.value).isEqualTo(1.0f)
    }

    @Test
    fun setMasterVolume_below_zero_is_coerced_to_zero() {
        // Act
        sceneAudioEngine.setMasterVolume(-0.5f)

        // Assert
        assertThat(sceneAudioEngine.masterVolume.value).isEqualTo(0.0f)
    }

    @Test
    fun setMasterVolume_affects_all_category_players() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(any()) } returnsMany listOf(trackPlayer1, trackPlayer2)

        val player1 = sceneAudioEngine.getCategoryPlayer(category1)
        val player2 = sceneAudioEngine.getCategoryPlayer(category2)

        // Both at mix 1.0, master 1.0 -> effective 1.0
        player1.playTrack("track1")
        player2.playTrack("track2")

        // Act
        sceneAudioEngine.setMasterVolume(0.5f)

        // Trigger recalculation by setting mix volumes
        player1.setMixVolume(1.0f)
        player2.setMixVolume(1.0f)

        // Assert
        // Both should now be at effective volume 0.5 (mix 1.0 * master 0.5)
        verify { trackPlayer1.setVolume(0.5f) }
        verify { trackPlayer2.setVolume(0.5f) }
    }

    @Test
    fun releaseAll_releases_all_category_players() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        val trackPlayer2: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(any()) } returnsMany listOf(trackPlayer1, trackPlayer2)

        val player1 = sceneAudioEngine.getCategoryPlayer(category1)
        val player2 = sceneAudioEngine.getCategoryPlayer(category2)
        player1.playTrack("track1")
        player2.playTrack("track2")

        // Act
        sceneAudioEngine.releaseAll()

        // Assert
        verify(exactly = 1) { trackPlayer1.stopTrack() }
        verify(exactly = 1) { trackPlayer1.release() }
        verify(exactly = 1) { trackPlayer2.stopTrack() }
        verify(exactly = 1) { trackPlayer2.release() }

        // All players should be removed
        val newPlayer1 = sceneAudioEngine.getCategoryPlayer(category1)
        assertThat(newPlayer1).isNotSameAs(player1)
    }

    @Test
    fun master_volume_is_used_in_effective_volume_calculation() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true) {
            every { isPlaying } returns MutableStateFlow(true)
        }
        every { trackFactory.createLoopableTrackPlayer(any()) } returns trackPlayer

        // Set master to 0.8
        sceneAudioEngine.setMasterVolume(0.8f)

        val player = sceneAudioEngine.getCategoryPlayer(category1)
        player.playTrack(track1)

        // Act
        // Set category mix to 0.5
        player.setMixVolume(0.5f)

        // Assert
        // Effective volume should be 0.5 * 0.8 = 0.4
        verify { trackPlayer.setVolume(0.4f) }
    }
}
