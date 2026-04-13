package com.example.rpgaudiomixer.app.data.audiotrack

import com.example.rpgaudiomixer.app.data.local.dao.AudioTrackDao
import com.example.rpgaudiomixer.app.data.local.entities.AudioTrackEntity
import com.example.rpgaudiomixer.app.domain.model.AudioTrackType
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AudioTrackRepositoryImplTest {

    private val mockDao: AudioTrackDao = mockk()
    private val sut = AudioTrackRepositoryImpl(mockDao)

    @Test
    fun `observeAll maps entities to domain models`() = runTest {
        // Arrange
        val entity = AudioTrackEntity(
            id = 1, name = "Rain Loop", localPath = "/data/audio_library/rain.mp3",
            originalUri = "content://media/rain.mp3", type = "SOUNDSCAPE",
            isDeleted = false, deletedAt = null
        )
        every { mockDao.observeAll() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(1)
        val track = result[0]
        assertThat(track.id).isEqualTo(1)
        assertThat(track.name).isEqualTo("Rain Loop")
        assertThat(track.localPath).isEqualTo("/data/audio_library/rain.mp3")
        assertThat(track.originalUri).isEqualTo("content://media/rain.mp3")
        assertThat(track.type).isEqualTo(AudioTrackType.SOUNDSCAPE)
        assertThat(track.isDeleted).isFalse()
        assertThat(track.deletedAt).isNull()
    }

    @Test
    fun `observeAll maps multiple entities`() = runTest {
        // Arrange
        val entities = listOf(
            AudioTrackEntity(
                id = 1, name = "Rain Loop", localPath = "/data/rain.mp3",
                originalUri = "content://rain.mp3", type = "SOUNDSCAPE"
            ),
            AudioTrackEntity(
                id = 2, name = "Sword Clash", localPath = "/data/sword.mp3",
                originalUri = "content://sword.mp3", type = "FX"
            )
        )
        every { mockDao.observeAll() } returns flowOf(entities)

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].name).isEqualTo("Rain Loop")
        assertThat(result[0].type).isEqualTo(AudioTrackType.SOUNDSCAPE)
        assertThat(result[1].name).isEqualTo("Sword Clash")
        assertThat(result[1].type).isEqualTo(AudioTrackType.FX)
    }

    @Test
    fun `observeByType passes correct type string to dao`() = runTest {
        // Arrange
        val entity = AudioTrackEntity(
            id = 1, name = "Thunder", localPath = "/data/thunder.mp3",
            originalUri = "content://thunder.mp3", type = "FX"
        )
        every { mockDao.observeByType("FX") } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeByType(AudioTrackType.FX).first()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0].type).isEqualTo(AudioTrackType.FX)
        assertThat(result[0].name).isEqualTo("Thunder")
    }

    @Test
    fun `observeByType with SOUNDSCAPE passes correct string`() = runTest {
        // Arrange
        val entity = AudioTrackEntity(
            id = 2, name = "Forest Ambience", localPath = "/data/forest.mp3",
            originalUri = "content://forest.mp3", type = "SOUNDSCAPE"
        )
        every { mockDao.observeByType("SOUNDSCAPE") } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeByType(AudioTrackType.SOUNDSCAPE).first()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0].type).isEqualTo(AudioTrackType.SOUNDSCAPE)
    }

    @Test
    fun `observeDeleted maps deleted entities to domain models`() = runTest {
        // Arrange
        val entity = AudioTrackEntity(
            id = 5, name = "Old Track", localPath = "/data/old.mp3",
            originalUri = "content://old.mp3", type = "SOUNDSCAPE",
            isDeleted = true, deletedAt = 9000L
        )
        every { mockDao.observeDeleted() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeDeleted().first()

        // Assert
        assertThat(result).hasSize(1)
        val track = result[0]
        assertThat(track.id).isEqualTo(5)
        assertThat(track.name).isEqualTo("Old Track")
        assertThat(track.isDeleted).isTrue()
        assertThat(track.deletedAt).isEqualTo(9000L)
    }

    @Test
    fun `createTrack upserts entity and returns id`() = runTest {
        // Arrange
        val entitySlot = slot<AudioTrackEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 11L

        // Act
        val result = sut.createTrack(
            name = "Wind Howl",
            localPath = "/data/audio_library/wind.mp3",
            originalUri = "content://media/wind.mp3",
            type = AudioTrackType.SOUNDSCAPE
        )

        // Assert
        assertThat(result).isEqualTo(11L)
        coVerify { mockDao.upsert(any()) }
        assertThat(entitySlot.captured.name).isEqualTo("Wind Howl")
        assertThat(entitySlot.captured.localPath).isEqualTo("/data/audio_library/wind.mp3")
        assertThat(entitySlot.captured.originalUri).isEqualTo("content://media/wind.mp3")
        assertThat(entitySlot.captured.type).isEqualTo("SOUNDSCAPE")
    }

    @Test
    fun `createTrack with FX type stores correct type string`() = runTest {
        // Arrange
        val entitySlot = slot<AudioTrackEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 12L

        // Act
        sut.createTrack(
            name = "Explosion",
            localPath = "/data/audio_library/boom.mp3",
            originalUri = "content://media/boom.mp3",
            type = AudioTrackType.FX
        )

        // Assert
        assertThat(entitySlot.captured.type).isEqualTo("FX")
    }

    @Test
    fun `softDelete delegates to dao softDelete`() = runTest {
        // Arrange
        coEvery { mockDao.softDelete(any()) } just Runs

        // Act
        sut.softDelete(42)

        // Assert
        coVerify { mockDao.softDelete(42) }
    }

    @Test
    fun `restore delegates to dao restore`() = runTest {
        // Arrange
        coEvery { mockDao.restore(any()) } just Runs

        // Act
        sut.restore(42)

        // Assert
        coVerify { mockDao.restore(42) }
    }

    @Test
    fun `permanentlyDelete delegates to dao permanentlyDelete`() = runTest {
        // Arrange
        coEvery { mockDao.permanentlyDelete(any()) } just Runs

        // Act
        sut.permanentlyDelete(42)

        // Assert
        coVerify { mockDao.permanentlyDelete(42) }
    }

    @Test
    fun `deleteAll delegates to dao deleteAll`() = runTest {
        // Arrange
        coEvery { mockDao.deleteAll() } just Runs

        // Act
        sut.deleteAll()

        // Assert
        coVerify { mockDao.deleteAll() }
    }
}
