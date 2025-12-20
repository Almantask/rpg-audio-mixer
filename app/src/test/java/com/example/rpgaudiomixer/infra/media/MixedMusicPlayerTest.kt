package com.example.rpgaudiomixer.infra.media

import com.example.rpgaudiomixer.domain.media.MixedMusicPlayerImpl
import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import com.example.rpgaudiomixer.domain.storage.TrackRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class MixedMusicPlayerTest {

    private val trackFactory: TrackFactory = mockk(relaxed = true)
    private val trackRepository: TrackRepository = mockk(relaxed = true)
    private val exoMixedMusicPlayer = MixedMusicPlayerImpl(trackFactory, trackRepository)

    constructor(){
        every { trackRepository.getTrackFilePath(any()) } answers { firstArg() as String }
    }

    private val track1 = "test-sound-1"
    private val track2 = "test-sound-2"

    @Test
    fun playSingleSound_looks_up_a_track() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        every { trackFactory.createOneTimeTrackPlayer(track1) } returns trackPlayer

        // Act
        exoMixedMusicPlayer.playSingleSound(track1)

        // Assert
        verify(exactly = 1) { trackRepository.getTrackFilePath(track1) }
    }

    @Test
    fun playSingleSound_creates_a_new_track_player_and_plays_the_track() {
        // Arrange
        val trackPlayer: TrackPlayer = mockk(relaxed = true)
        every { trackFactory.createOneTimeTrackPlayer(track1) } returns trackPlayer

        // Act
        exoMixedMusicPlayer.playSingleSound(track1)

        // Assert
        verify(exactly = 1) { trackFactory.createOneTimeTrackPlayer(track1) }
        verify(exactly = 1) { trackPlayer.play() }
    }

    @Test
    fun playSingleSound_called_twice_for_the_same_track_creates_two_distinct_plays_both() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true)
        val trackPlayer2: TrackPlayer = mockk(relaxed = true)
        every { trackFactory.createOneTimeTrackPlayer(track1) } returnsMany listOf(trackPlayer1, trackPlayer2)

        // Act
        exoMixedMusicPlayer.playSingleSound(track1)
        exoMixedMusicPlayer.playSingleSound(track1)

        // Assert
        verify(exactly = 2) { trackFactory.createOneTimeTrackPlayer(track1) }
        verify(exactly = 1) { trackPlayer1.play() }
        verify(exactly = 1) { trackPlayer2.play() }
    }

    @Test
    fun playSingleSound_called_twice_for_different_tracks_creates_two_distinct_plays_both() {
        // Arrange
        val trackPlayer1: TrackPlayer = mockk(relaxed = true)
        val trackPlayer2: TrackPlayer = mockk(relaxed = true)
        every { trackFactory.createOneTimeTrackPlayer(track1) } returns trackPlayer1
        every { trackFactory.createOneTimeTrackPlayer(track2) } returns trackPlayer2

        // Act
        exoMixedMusicPlayer.playSingleSound(track1)
        exoMixedMusicPlayer.playSingleSound(track2)

        // Assert
        verify(exactly = 1) { trackFactory.createOneTimeTrackPlayer(track1) }
        verify(exactly = 1) { trackFactory.createOneTimeTrackPlayer(track2) }
        verify(exactly = 1) { trackPlayer1.play() }
        verify(exactly = 1) { trackPlayer2.play() }
    }
}