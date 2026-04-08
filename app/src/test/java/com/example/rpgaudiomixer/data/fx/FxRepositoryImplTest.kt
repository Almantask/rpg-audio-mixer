package com.example.rpgaudiomixer.data.fx

import com.example.rpgaudiomixer.data.local.FxTrackDao
import com.example.rpgaudiomixer.data.local.FxTrackEntity
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FxRepositoryImplTest {

    private val dao = FakeFxTrackDao()
    private val importer = FakeFxAudioImporter()
    private val repository: FxRepository = FxRepositoryImpl(
        fxTrackDao = dao,
        fxAudioImporter = importer,
    )

    @Test
    fun observeFxTracks_maps_entities_to_domain_models() = runTest {
        // Arrange
        dao.allTracksFlow.value = listOf(
            FxTrackEntity(
                id = 7L,
                name = "Wolf Howl",
                filePath = "file:///fx/wolf_howl.mp3",
                tags = "Combat,Nature",
                durationMs = 3200L,
                playCount = 2,
                isDemo = false,
                isDeleted = false,
            )
        )

        // Act
        val result = repository.observeFxTracks().first()

        // Assert
        assertThat(result).containsExactly(
            FxTrack(
                id = 7L,
                name = "Wolf Howl",
                filePath = "file:///fx/wolf_howl.mp3",
                tags = listOf("Combat", "Nature"),
                durationMs = 3200L,
                playCount = 2,
                isDemo = false,
            )
        )
    }

    @Test
    fun searchFxTracks_returns_matching_tracks() = runTest {
        // Arrange
        dao.searchTracksFlow.value = listOf(
            FxTrackEntity(
                id = 9L,
                name = "Thunder Crack",
                filePath = "file:///fx/thunder_crack.mp3",
                tags = "Weather",
                durationMs = 4100L,
                playCount = 0,
                isDemo = false,
                isDeleted = false,
            )
        )

        // Act
        val result = repository.searchFxTracks("thunder").first()

        // Assert
        assertThat(result).containsExactly(
            FxTrack(
                id = 9L,
                name = "Thunder Crack",
                filePath = "file:///fx/thunder_crack.mp3",
                tags = listOf("Weather"),
                durationMs = 4100L,
                playCount = 0,
                isDemo = false,
            )
        )
    }

    @Test
    fun importFxTrack_uses_importer_and_persists_the_new_track() = runTest {
        // Arrange
        importer.nextImportedTrack = ImportedFxAudioFile(
            displayName = "wolf_howl.mp3",
            storedPath = "file:///fx/wolf_howl.mp3",
            durationMs = 3000L,
        )

        // Act
        val result = repository.importFxTrack("content://device/wolf_howl.mp3")

        // Assert
        assertThat(result).isEqualTo(
            FxTrack(
                id = 51L,
                name = "wolf_howl.mp3",
                filePath = "file:///fx/wolf_howl.mp3",
                tags = emptyList(),
                durationMs = 3000L,
                playCount = 0,
                isDemo = false,
            )
        )
        assertThat(dao.upsertedTracks).containsExactly(
            FxTrackEntity(
                id = 0L,
                name = "wolf_howl.mp3",
                filePath = "file:///fx/wolf_howl.mp3",
                tags = "",
                durationMs = 3000L,
                playCount = 0,
                isDemo = false,
                isDeleted = false,
            )
        )
    }

    @Test
    fun updateFxTrack_persists_the_edited_values() = runTest {
        // Arrange
        val track = FxTrack(
            id = 7L,
            name = "Wolf Howl",
            filePath = "file:///fx/wolf_howl.mp3",
            tags = listOf("Combat", "Creature"),
            durationMs = 3200L,
            playCount = 4,
            isDemo = false,
        )

        // Act
        repository.updateFxTrack(track)

        // Assert
        assertThat(dao.upsertedTracks).containsExactly(
            FxTrackEntity(
                id = 7L,
                name = "Wolf Howl",
                filePath = "file:///fx/wolf_howl.mp3",
                tags = "Combat,Creature",
                durationMs = 3200L,
                playCount = 4,
                isDemo = false,
                isDeleted = false,
            )
        )
    }

    @Test
    fun softDeleteTrack_marks_the_track_as_deleted() = runTest {
        // Arrange

        // Act
        repository.softDeleteFxTrack(12L)

        // Assert
        assertThat(dao.softDeletedIds).containsExactly(12L)
    }

    @Test
    fun seedDemoFxTracks_creates_one_hundred_demo_tracks() = runTest {
        // Arrange

        // Act
        repository.seedDemoFxTracks()
        val hasDemoTracks = repository.observeHasDemoFxTracks().first()

        // Assert
        assertThat(dao.upsertedTrackLists.single()).hasSize(100)
        assertThat(dao.upsertedTrackLists.single().all { it.isDemo }).isTrue()
        assertThat(hasDemoTracks).isTrue()
    }

    private class FakeFxTrackDao : FxTrackDao {
        val allTracksFlow = MutableStateFlow<List<FxTrackEntity>>(emptyList())
        val searchTracksFlow = MutableStateFlow<List<FxTrackEntity>>(emptyList())
        val demoAvailabilityFlow = MutableStateFlow(false)
        val upsertedTracks = mutableListOf<FxTrackEntity>()
        val upsertedTrackLists = mutableListOf<List<FxTrackEntity>>()
        val softDeletedIds = mutableListOf<Long>()

        override fun observeAll(): Flow<List<FxTrackEntity>> = allTracksFlow

        override fun search(query: String): Flow<List<FxTrackEntity>> = searchTracksFlow

        override fun observeDemoContentAvailable(): Flow<Boolean> = demoAvailabilityFlow

        override suspend fun upsert(track: FxTrackEntity): Long {
            upsertedTracks += track
            return if (track.id > 0L) track.id else 51L
        }

        override suspend fun upsertAll(tracks: List<FxTrackEntity>) {
            upsertedTrackLists += tracks
            demoAvailabilityFlow.value = tracks.any(FxTrackEntity::isDemo)
        }

        override suspend fun softDelete(trackId: Long) {
            softDeletedIds += trackId
        }
    }

    private class FakeFxAudioImporter : FxAudioImporter {
        var nextImportedTrack: ImportedFxAudioFile = ImportedFxAudioFile(
            displayName = "default.mp3",
            storedPath = "file:///fx/default.mp3",
            durationMs = 1000L,
        )

        override suspend fun importAudio(sourceUri: String): ImportedFxAudioFile = nextImportedTrack
    }
}
