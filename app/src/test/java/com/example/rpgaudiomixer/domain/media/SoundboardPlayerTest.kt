package com.example.rpgaudiomixer.domain.media

import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.library.FxRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SoundboardPlayerTest {

    private val trackFactory = mockk<TrackFactory>()
    private val fxRepository = mockk<FxRepository>(relaxed = true)
    private val trackPlayer = mockk<TrackPlayer>(relaxed = true)
    private lateinit var soundboardPlayer: SoundboardPlayer

    @BeforeEach
    fun setUp() {
        every { trackFactory.createOneTimeTrackPlayer(any()) } returns trackPlayer
        soundboardPlayer = SoundboardPlayer(trackFactory, fxRepository)
    }

    @Test
    fun `triggerFx plays track and increments play count`() = runTest {
        val fx = FxTrack(id = 789, name = "F1", filePath = "fx.mp3", tags = emptyList(), durationMs = 1000)
        
        soundboardPlayer.triggerFx(fx)

        verify { trackPlayer.play() }
        coVerify { fxRepository.incrementPlayCount(789) }
    }
}
