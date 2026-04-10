package com.example.rpgaudiomixer.data.fx

import com.example.rpgaudiomixer.data.fx.local.FxTrackDao
import com.example.rpgaudiomixer.data.fx.local.FxTrackEntity
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.infra.media.AudioMetadataReader
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FxRepositoryImplTest {

    private val trackDao: FxTrackDao = mockk()
    private val audioMetadataReader: AudioMetadataReader = mockk()
    private val repository = FxRepositoryImpl(trackDao, audioMetadataReader)

    @Test
    fun observeTracks_maps_entities_to_domain_models() = runTest {
        // Arrange
        every { trackDao.observeAll() } returns flowOf(
            listOf(
                FxTrackEntity(
                    id = 1L,
                    name = "Wolf Howl",
                    filePath = "content://wolf_howl",
                    tags = "Combat,Creature",
                    durationMs = 3_000L,
                    playCount = 2,
                    isDemoContent = false,
                ),
            ),
        )

        // Act
        val result = repository.observeTracks().first()

        // Assert
        assertThat(result).containsExactly(
            FxTrack(
                id = 1L,
                name = "Wolf Howl",
                filePath = "content://wolf_howl",
                tags = listOf("Combat", "Creature"),
                durationMs = 3_000L,
                playCount = 2,
                isDemoContent = false,
            ),
        )
    }

    @Test
    fun importTrack_given_audio_cannot_be_read_returns_failure_and_does_not_insert() = runTest {
        // Arrange
        val filePath = "content://fake.mp3"
        coEvery { audioMetadataReader.readDurationMillis(filePath) } returns Result.failure(
            IllegalArgumentException("Unreadable"),
        )

        // Act
        val result = repository.importTrack(
            name = "fake.mp3",
            filePath = filePath,
        )

        // Assert
        assertThat(result.isFailure).isTrue()
        coVerify(exactly = 0) { trackDao.insert(any()) }
    }

    @Test
    fun observeMostPlayedTrack_returns_the_top_fx_track() = runTest {
        // Arrange
        every { trackDao.observeMostPlayedTrack() } returns flowOf(
            FxTrackEntity(
                id = 7L,
                name = "Thunder Crack",
                filePath = "content://thunder",
                tags = "Magic,Storm",
                durationMs = 1_500L,
                playCount = 12,
                isDemoContent = false,
            ),
        )

        // Act
        val result = repository.observeMostPlayedTrack().first()

        // Assert
        assertThat(result).isEqualTo(
            FxTrack(
                id = 7L,
                name = "Thunder Crack",
                filePath = "content://thunder",
                tags = listOf("Magic", "Storm"),
                durationMs = 1_500L,
                playCount = 12,
                isDemoContent = false,
            ),
        )
    }

    @Test
    fun deleteTrack_soft_deletes_the_track() = runTest {
        // Arrange
        coEvery { trackDao.softDeleteById(7L, 300L) } returns Unit

        // Act
        repository.deleteTrack(trackId = 7L, deletedAtMillis = 300L)

        // Assert
        coVerify(exactly = 1) { trackDao.softDeleteById(7L, 300L) }
    }
}
