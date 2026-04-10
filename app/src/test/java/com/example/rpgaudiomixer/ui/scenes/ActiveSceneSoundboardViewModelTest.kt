package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.media.FakeTrackFactory
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.scene.SceneFxRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveSceneSoundboardViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun triggerFx_marks_the_fx_as_playing() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneFxRepository(
            linkedFx = listOf(sceneFx(trackId = 7L, name = "Thunder Crack")),
            availableFx = emptyList(),
        )
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneFxRepository = repository,
            soundboardPlayer = SoundboardPlayer(trackFactory = FakeTrackFactory()),
        )
        advanceUntilIdle()

        // Act
        viewModel.triggerFx(7L)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundboardUiState.Success
        assertThat(state.fxButtons.single().playingInstanceCount).isEqualTo(1)
        assertThat(repository.incrementedTrackIds).containsExactly(7L)
    }

    @Test
    fun triggerFx_twice_retriggers_without_clearing_the_first_instance() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneFxRepository(
            linkedFx = listOf(sceneFx(trackId = 7L, name = "Thunder Crack")),
            availableFx = emptyList(),
        )
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneFxRepository = repository,
            soundboardPlayer = SoundboardPlayer(trackFactory = FakeTrackFactory()),
        )
        advanceUntilIdle()

        // Act
        viewModel.triggerFx(7L)
        viewModel.triggerFx(7L)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundboardUiState.Success
        assertThat(state.fxButtons.single().playingInstanceCount).isEqualTo(2)
    }

    @Test
    fun stopFx_removes_one_playing_instance_for_the_selected_track() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneFxRepository(
            linkedFx = listOf(sceneFx(trackId = 7L, name = "Thunder Crack")),
            availableFx = emptyList(),
        )
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneFxRepository = repository,
            soundboardPlayer = SoundboardPlayer(trackFactory = FakeTrackFactory()),
        )
        advanceUntilIdle()
        viewModel.triggerFx(7L)
        viewModel.triggerFx(7L)
        advanceUntilIdle()

        // Act
        viewModel.stopFx(7L)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundboardUiState.Success
        assertThat(state.fxButtons.single().playingInstanceCount).isEqualTo(1)
    }

    @Test
    fun addFx_imports_the_effect_and_removes_it_from_the_picker_options() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneFxRepository(
            linkedFx = emptyList(),
            availableFx = listOf(
                fxTrack(id = 7L, name = "Thunder Crack"),
                fxTrack(id = 8L, name = "Wolf Howl"),
            ),
        )
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneFxRepository = repository,
            soundboardPlayer = SoundboardPlayer(trackFactory = FakeTrackFactory()),
        )
        advanceUntilIdle()

        // Act
        viewModel.addFx(7L)
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value as ActiveSceneSoundboardUiState.Success
        assertThat(state.fxButtons.map { it.name }).isEqualTo(listOf("Thunder Crack"))
        assertThat(state.availableFxToAdd.map { it.name }).isEqualTo(listOf("Wolf Howl"))
    }

    @Test
    fun setMasterVolume_updates_all_active_instances() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val trackFactory = FakeTrackFactory()
        val repository = FakeSceneFxRepository(
            linkedFx = listOf(sceneFx(trackId = 7L, name = "Thunder Crack")),
            availableFx = emptyList(),
        )
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneFxRepository = repository,
            soundboardPlayer = SoundboardPlayer(trackFactory = trackFactory),
        )
        advanceUntilIdle()
        viewModel.triggerFx(7L)
        advanceUntilIdle()

        // Act
        viewModel.setMasterVolume(0.4f)
        advanceUntilIdle()

        // Assert
        assertThat(trackFactory.oneTimePlayers.single().volumeHistory.last()).isEqualTo(0.4f)
    }

    @Test
    fun reorderFx_persists_the_new_display_order() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneFxRepository(
            linkedFx = listOf(
                sceneFx(trackId = 7L, name = "Thunder Crack"),
                sceneFx(trackId = 8L, name = "Wolf Howl").copy(displayOrder = 1),
            ),
            availableFx = emptyList(),
        )
        val viewModel = ActiveSceneSoundboardViewModel(
            sceneId = 5L,
            sceneRepository = FakeSceneRepository(),
            sceneFxRepository = repository,
            soundboardPlayer = SoundboardPlayer(trackFactory = FakeTrackFactory()),
        )
        advanceUntilIdle()

        // Act
        viewModel.reorderFx(listOf(8L, 7L))
        advanceUntilIdle()

        // Assert
        assertThat(repository.lastReorderedFxTrackIds).isEqualTo(listOf(8L, 7L))
    }

    private class FakeSceneRepository : SceneRepository {
        override fun observeScenes(): Flow<List<Scene>> = flowOf(emptyList())

        override fun observeScene(sceneId: Long): Flow<Scene?> {
            return flowOf(Scene(id = sceneId, name = "Moonlit Keep", description = null, tags = emptyList()))
        }

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long {
            error("Not needed in this test")
        }

        override suspend fun deleteScene(sceneId: Long, deletedAtMillis: Long) {
            error("Not needed in this test")
        }

        override suspend fun updateMasterVolume(sceneId: Long, masterVolume: Float) {
            error("Not needed in this test")
        }
    }

    private class FakeSceneFxRepository(
        linkedFx: List<SceneFx>,
        availableFx: List<FxTrack>,
    ) : SceneFxRepository {
        private val linkedFlow = MutableStateFlow(linkedFx)
        private val availableFlow = MutableStateFlow(availableFx)
        val incrementedTrackIds = mutableListOf<Long>()
        var lastReorderedFxTrackIds: List<Long> = emptyList()

        override fun observeSceneFx(sceneId: Long): Flow<List<SceneFx>> = linkedFlow

        override fun observeAvailableFx(sceneId: Long): Flow<List<FxTrack>> = availableFlow

        override suspend fun addFxToScene(sceneId: Long, fxTrackId: Long) {
            val track = availableFlow.value.first { it.id == fxTrackId }
            linkedFlow.value = linkedFlow.value + sceneFx(trackId = fxTrackId, name = track.name).copy(
                displayOrder = linkedFlow.value.size,
            )
            availableFlow.value = availableFlow.value.filterNot { it.id == fxTrackId }
        }

        override suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long) {
            linkedFlow.value = linkedFlow.value.filterNot { it.fxTrackId == fxTrackId }
        }

        override suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>) {
            lastReorderedFxTrackIds = orderedFxTrackIds
            linkedFlow.value = orderedFxTrackIds.mapIndexedNotNull { index, fxTrackId ->
                linkedFlow.value.firstOrNull { it.fxTrackId == fxTrackId }?.copy(displayOrder = index)
            }
        }

        override suspend fun incrementTrackPlayCount(trackId: Long) {
            incrementedTrackIds += trackId
        }
    }
}

private fun sceneFx(
    trackId: Long,
    name: String,
): SceneFx {
    return SceneFx(
        sceneId = 5L,
        fxTrackId = trackId,
        name = name,
        filePath = "content://$trackId",
        tags = emptyList(),
        durationMs = 1_000L,
        playCount = 0,
        isDemoContent = false,
        displayOrder = 0,
    )
}

private fun fxTrack(
    id: Long,
    name: String,
): FxTrack {
    return FxTrack(
        id = id,
        name = name,
        filePath = "content://$id",
        tags = emptyList(),
        durationMs = 1_000L,
        playCount = 0,
        isDemoContent = false,
    )
}
