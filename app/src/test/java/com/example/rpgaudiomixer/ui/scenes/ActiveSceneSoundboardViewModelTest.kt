package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.media.RecordingTrackFactory
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ActiveSceneSoundboardViewModelTest {

    @Test
    fun init_exposes_scene_name_and_ordered_fx_buttons() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 0)
            sceneFxFlow.value = listOf(
                sceneFx(trackId = 20L, name = "Wolf Howl", displayOrder = 1),
                sceneFx(trackId = 10L, name = "Thunder Crack", displayOrder = 0),
            )
        }
        val fxRepository = FakeFxRepository()

        // Act
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 4L,
            sceneRepository = sceneRepository,
            fxRepository = fxRepository,
            soundboardPlayer = SoundboardPlayer(RecordingTrackFactory()),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.sceneName).isEqualTo("Forest Path")
        assertThat(viewModel.uiState.value.fxButtons.map { it.track.name }).containsExactly("Thunder Crack", "Wolf Howl")
    }

    @Test
    fun triggerFx_marks_the_button_as_playing_and_increments_the_instance_count() = runTest {
        // Arrange
        val trackFactory = RecordingTrackFactory()
        val viewModel = buildViewModel(
            testScheduler = testScheduler,
            trackFactory = trackFactory,
        )

        // Act
        viewModel.triggerFx(10L)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.fxButtons.single().playingInstanceCount).isEqualTo(1)
        assertThat(viewModel.uiState.value.fxButtons.single().isPlaying).isTrue()
        assertThat(trackFactory.createdOneTimePlayers.single().track).isEqualTo("thunder_crack")
    }

    @Test
    fun triggerFx_increments_the_selected_track_play_count() = runTest {
        // Arrange
        val fxRepository = FakeFxRepository().apply {
            tracksFlow.value = listOf(fxTrack(id = 10L, name = "Thunder Crack", durationMs = 1_000L))
        }
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 4L,
            sceneRepository = FakeSceneRepository().apply {
                sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 0)
                sceneFxFlow.value = listOf(sceneFx(trackId = 10L, name = "Thunder Crack", durationMs = 1_000L))
            },
            fxRepository = fxRepository,
            soundboardPlayer = SoundboardPlayer(RecordingTrackFactory()),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.triggerFx(10L)
        advanceUntilIdle()

        // Assert
        assertThat(fxRepository.incrementedTrackIds).containsExactly(10L)
    }

    @Test
    fun triggerFx_when_the_limit_is_exceeded_removes_the_oldest_instance_from_the_button_state() = runTest {
        // Arrange
        val trackFactory = RecordingTrackFactory()
        val viewModel = buildViewModel(
            testScheduler = testScheduler,
            trackFactory = trackFactory,
        )

        // Act
        repeat(6) {
            viewModel.triggerFx(10L)
        }
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.fxButtons.single().playingInstanceCount).isEqualTo(5)
        assertThat(trackFactory.createdOneTimePlayers.first().stopCalls).isEqualTo(1)
        assertThat(trackFactory.createdOneTimePlayers.first().releaseCalls).isEqualTo(1)
    }

    @Test
    fun stopFx_stops_the_most_recent_instance_for_the_selected_track() = runTest {
        // Arrange
        val trackFactory = RecordingTrackFactory()
        val viewModel = buildViewModel(
            testScheduler = testScheduler,
            trackFactory = trackFactory,
        )
        viewModel.triggerFx(10L)
        viewModel.triggerFx(10L)
        advanceUntilIdle()

        // Act
        viewModel.stopFx(10L)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.fxButtons.single().playingInstanceCount).isEqualTo(1)
        assertThat(trackFactory.createdOneTimePlayers[0].stopCalls).isEqualTo(0)
        assertThat(trackFactory.createdOneTimePlayers[1].stopCalls).isEqualTo(1)
    }

    @Test
    fun addFx_updates_the_selection_state_and_persists_the_scene_link() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 0)
        }
        val fxRepository = FakeFxRepository().apply {
            tracksFlow.value = listOf(
                fxTrack(id = 10L, name = "Thunder Crack"),
                fxTrack(id = 20L, name = "Wolf Howl"),
            )
        }
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 4L,
            sceneRepository = sceneRepository,
            fxRepository = fxRepository,
            soundboardPlayer = SoundboardPlayer(RecordingTrackFactory()),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()
        viewModel.showAddFxSheet()

        // Act
        viewModel.addFx(20L)
        advanceUntilIdle()

        // Assert
        assertThat(sceneRepository.addedFx).containsExactly(4L to 20L)
        assertThat(viewModel.uiState.value.selectionOptions.first { it.track.id == 20L }.isAdded).isTrue()
    }

    @Test
    fun importNewFx_adds_the_imported_track_to_the_selection_options() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 0)
        }
        val fxRepository = FakeFxRepository()
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 4L,
            sceneRepository = sceneRepository,
            fxRepository = fxRepository,
            soundboardPlayer = SoundboardPlayer(RecordingTrackFactory()),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()
        viewModel.showAddFxSheet()

        // Act
        viewModel.importNewFx("file:///fx/cannon_fire.mp3")
        advanceUntilIdle()

        // Assert
        assertThat(fxRepository.importRequests).containsExactly("file:///fx/cannon_fire.mp3")
        assertThat(viewModel.uiState.value.selectionOptions.map { it.track.name }).contains("cannon_fire.mp3")
    }

    @Test
    fun init_exposes_play_count_for_each_fx_selection_option() = runTest {
        // Arrange
        val fxRepository = FakeFxRepository().apply {
            tracksFlow.value = listOf(
                fxTrack(id = 10L, name = "Thunder Crack", durationMs = 1_000L).copy(playCount = 9),
                fxTrack(id = 20L, name = "Wolf Howl", durationMs = 800L).copy(playCount = 3),
            )
        }

        // Act
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 4L,
            sceneRepository = FakeSceneRepository().apply {
                sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 0)
            },
            fxRepository = fxRepository,
            soundboardPlayer = SoundboardPlayer(RecordingTrackFactory()),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.selectionOptions.associate { it.track.id to it.playCount }).isEqualTo(
            mapOf(
                10L to 9,
                20L to 3,
            )
        )
    }

    @Test
    fun reorderFx_updates_display_order_and_persists_the_new_sequence() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 0)
            sceneFxFlow.value = listOf(
                sceneFx(trackId = 10L, name = "Thunder Crack", displayOrder = 0),
                sceneFx(trackId = 20L, name = "Wolf Howl", displayOrder = 1),
            )
        }
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 4L,
            sceneRepository = sceneRepository,
            fxRepository = FakeFxRepository(),
            soundboardPlayer = SoundboardPlayer(RecordingTrackFactory()),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Act
        viewModel.reorderFx(listOf(20L, 10L))
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.fxButtons.map { it.track.id }).containsExactly(20L, 10L)
        assertThat(sceneRepository.reorderedFxIds).containsExactly(listOf(20L, 10L))
    }

    @Test
    fun triggerFx_clears_the_playing_state_after_the_track_duration_elapses() = runTest {
        // Arrange
        val viewModel = buildViewModel(testScheduler = testScheduler)

        // Act
        viewModel.triggerFx(10L)
        advanceUntilIdle()
        advanceTimeBy(1_500L)
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.fxButtons.single().playingInstanceCount).isEqualTo(0)
        assertThat(viewModel.uiState.value.fxButtons.single().isPlaying).isFalse()
    }

    private fun buildViewModel(
        testScheduler: kotlinx.coroutines.test.TestCoroutineScheduler,
        trackFactory: RecordingTrackFactory = RecordingTrackFactory(),
    ): ActiveSceneSoundboardViewModel {
        val sceneRepository = FakeSceneRepository().apply {
            sceneFlow.value = Scene(4L, "Forest Path", null, emptyList(), 0)
            sceneFxFlow.value = listOf(sceneFx(trackId = 10L, name = "Thunder Crack", durationMs = 1_000L))
        }
        val fxRepository = FakeFxRepository().apply {
            tracksFlow.value = listOf(fxTrack(id = 10L, name = "Thunder Crack", durationMs = 1_000L))
        }
        return ActiveSceneSoundboardViewModel(
            sceneId = 4L,
            sceneRepository = sceneRepository,
            fxRepository = fxRepository,
            soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
    }

    private fun sceneFx(
        trackId: Long,
        name: String,
        displayOrder: Int = 0,
        durationMs: Long = 900L,
    ) = SceneFx(
        sceneId = 4L,
        track = fxTrack(
            id = trackId,
            name = name,
            durationMs = durationMs,
        ),
        displayOrder = displayOrder,
    )

    private fun fxTrack(
        id: Long,
        name: String,
        durationMs: Long = 900L,
    ) = FxTrack(
        id = id,
        name = name,
        filePath = name.lowercase().replace(' ', '_'),
        tags = emptyList(),
        durationMs = durationMs,
        playCount = 0,
        isDemo = false,
    )

    private class FakeSceneRepository : SceneRepository {
        val sceneFlow = MutableStateFlow<Scene?>(null)
        val sceneSoundscapesFlow = MutableStateFlow<List<SceneSoundscape>>(emptyList())
        val sceneFxFlow = MutableStateFlow<List<SceneFx>>(emptyList())
        val addedFx = mutableListOf<Pair<Long, Long>>()
        val reorderedFxIds = mutableListOf<List<Long>>()

        override fun observeScenes(): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeScene(sceneId: Long): Flow<Scene?> = sceneFlow

        override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeSoundscapesForScene(sceneId: Long): Flow<List<SceneSoundscape>> = sceneSoundscapesFlow

        override fun observeFxForScene(sceneId: Long): Flow<List<SceneFx>> = sceneFxFlow

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long = 0L

        override suspend fun updateScene(
            sceneId: Long,
            name: String,
            description: String?,
            tags: List<String>,
        ) = Unit

        override suspend fun deleteScene(sceneId: Long) = Unit

        override suspend fun linkScenesToSession(sessionId: Long, sceneIds: List<Long>) = Unit

        override suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long) = Unit

        override suspend fun addSoundscapeToScene(sceneId: Long, categoryId: Long) = Unit

        override suspend fun updateSoundscapeInScene(
            sceneId: Long,
            categoryId: Long,
            displayOrder: Int,
            mixVolume: Float,
            intensityLevel: IntensityLevel,
        ) = Unit

        override suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>) = Unit

        override suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long) = Unit

        override suspend fun addFxToScene(sceneId: Long, fxTrackId: Long) {
            addedFx += sceneId to fxTrackId
        }

        override suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>) {
            reorderedFxIds += orderedFxTrackIds
        }

        override suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long) = Unit
    }

    private class FakeFxRepository : FxRepository {
        val tracksFlow = MutableStateFlow<List<FxTrack>>(emptyList())
        val demoAvailabilityFlow = MutableStateFlow(false)
        val importRequests = mutableListOf<String>()
        val incrementedTrackIds = mutableListOf<Long>()

        override fun observeFxTracks(): Flow<List<FxTrack>> = tracksFlow

        override fun searchFxTracks(query: String): Flow<List<FxTrack>> = tracksFlow

        override fun observeHasDemoFxTracks(): Flow<Boolean> = demoAvailabilityFlow

        override suspend fun importFxTrack(sourceUri: String): FxTrack {
            importRequests += sourceUri
            val track = FxTrack(
                id = 99L,
                name = sourceUri.substringAfterLast('/'),
                filePath = sourceUri,
                tags = emptyList(),
                durationMs = 900L,
                playCount = 0,
                isDemo = false,
            )
            tracksFlow.value = tracksFlow.value + track
            return track
        }

        override suspend fun updateFxTrack(track: FxTrack) = Unit

        override suspend fun softDeleteFxTrack(trackId: Long) = Unit

        override suspend fun seedDemoFxTracks() = Unit

        override suspend fun incrementPlayCount(trackId: Long) {
            incrementedTrackIds += trackId
        }
    }
}
