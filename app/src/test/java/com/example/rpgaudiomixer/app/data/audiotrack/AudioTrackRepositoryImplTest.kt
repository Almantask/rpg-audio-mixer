package com.example.rpgaudiomixer.app.data.audiotrack

import com.example.rpgaudiomixer.app.data.local.dao.AudioTrackDao
import com.example.rpgaudiomixer.app.data.local.entities.AudioTrackEntity
import com.example.rpgaudiomixer.app.domain.model.AudioTrack
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
        val entity = AudioTrackEntity(id = 1, uri = "file:///audio/battle.mp3", displayName = "battle.mp3")
        every { mockDao.observeAll() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(1)
        val track = result[0]
        assertThat(track.id).isEqualTo(1)
        assertThat(track.uri).isEqualTo("file:///audio/battle.mp3")
        assertThat(track.displayName).isEqualTo("battle.mp3")
    }

    @Test
    fun `observeAll maps multiple entities`() = runTest {
        // Arrange
        val entities = listOf(
            AudioTrackEntity(id = 1, uri = "file:///audio/battle.mp3", displayName = "battle.mp3"),
            AudioTrackEntity(id = 2, uri = "file:///audio/tavern.ogg", displayName = "tavern.ogg")
        )
        every { mockDao.observeAll() } returns flowOf(entities)

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].displayName).isEqualTo("battle.mp3")
        assertThat(result[1].displayName).isEqualTo("tavern.ogg")
    }

    @Test
    fun `addTrack upserts entity with correct uri and displayName`() = runTest {
        // Arrange
        val entitySlot = slot<AudioTrackEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 1L

        // Act
        sut.addTrack(uri = "file:///audio/battle.mp3", displayName = "battle.mp3")

        // Assert
        coVerify { mockDao.upsert(any()) }
        assertThat(entitySlot.captured.uri).isEqualTo("file:///audio/battle.mp3")
        assertThat(entitySlot.captured.displayName).isEqualTo("battle.mp3")
    }

    @Test
    fun `deleteTrack performs soft delete by track id`() = runTest {
        // Arrange
        coEvery { mockDao.softDelete(42L) } just Runs
        val track = AudioTrack(id = 42, uri = "file:///audio/battle.mp3", displayName = "battle.mp3")

        // Act
        sut.deleteTrack(track)

        // Assert
        coVerify { mockDao.softDelete(42L) }
    }

    @Test
    fun `deleteAll delegates to dao`() = runTest {
        // Arrange
        coEvery { mockDao.deleteAll() } just Runs

        // Act
        sut.deleteAll()

        // Assert
        coVerify { mockDao.deleteAll() }
    }
}
