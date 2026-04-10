package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScenesViewModelTest {

    @Test
    fun init_exposes_scenes_from_the_repository() = runTest {
        // Arrange
        val repository = FakeSceneRepository()
        repository.emitScenes(
            listOf(
                Scene(2L, "Forest", "Dark woods", listOf("forest"), 0),
                Scene(1L, "Tavern", null, emptyList(), 0),
            )
        )

        // Act
        val viewModel = ScenesViewModel(
            sceneRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(
            ScenesUiState(
                isLoading = false,
                scenes = listOf(
                    Scene(2L, "Forest", "Dark woods", listOf("forest"), 0),
                    Scene(1L, "Tavern", null, emptyList(), 0),
                ),
            )
        )
    }

    @Test
    fun createScene_trims_inputs_and_delegates_to_the_repository() = runTest {
        // Arrange
        val repository = FakeSceneRepository()
        val viewModel = ScenesViewModel(
            sceneRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.createScene("  Tavern  ", "  Warm lights  ", " tavern, roleplay ")
        advanceUntilIdle()

        // Assert
        assertThat(repository.createdRequests).containsExactly(
            CreateSceneRequest(
                name = "Tavern",
                description = "Warm lights",
                tags = listOf("tavern", "roleplay"),
            )
        )
    }

    @Test
    fun deleteScene_delegates_to_the_repository() = runTest {
        // Arrange
        val repository = FakeSceneRepository()
        val viewModel = ScenesViewModel(
            sceneRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.deleteScene(8L)
        advanceUntilIdle()

        // Assert
        assertThat(repository.deletedSceneIds).containsExactly(8L)
    }

    @Test
    fun startCloningScene_populates_clone_state_with_a_copy_name() = runTest {
        // Arrange
        val repository = FakeSceneRepository()
        val viewModel = ScenesViewModel(
            sceneRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.startCloningScene(
            Scene(
                id = 8L,
                name = "Forest Night",
                description = "Owls and fog",
                tags = listOf("Forest"),
                soundscapeCount = 1,
            )
        )

        // Assert
        assertThat(viewModel.uiState.value.cloneState).isEqualTo(
            SceneCloneState(
                sceneId = 8L,
                name = "Forest Night Copy",
            )
        )
    }

    @Test
    fun cloneScene_trims_the_new_name_and_delegates_to_the_repository() = runTest {
        // Arrange
        val repository = FakeSceneRepository()
        val viewModel = ScenesViewModel(
            sceneRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        viewModel.startCloningScene(
            Scene(
                id = 8L,
                name = "Forest Night",
                description = "Owls and fog",
                tags = listOf("Forest"),
                soundscapeCount = 1,
            )
        )
        viewModel.updateCloneName("  Forest Dawn  ")

        // Act
        viewModel.cloneScene()
        advanceUntilIdle()

        // Assert
        assertThat(repository.clonedRequests).containsExactly(
            CloneSceneRequest(
                sceneId = 8L,
                name = "Forest Dawn",
            )
        )
        assertThat(viewModel.uiState.value.cloneState).isNull()
    }

    @Test
    fun startEditingScene_populates_editor_state_with_selected_and_custom_tags() = runTest {
        // Arrange
        val repository = FakeSceneRepository()
        val scene = Scene(
            id = 8L,
            name = "Tavern",
            description = "Warm lights",
            tags = listOf("Tavern", "boss fight"),
            soundscapeCount = 0,
        )
        val viewModel = ScenesViewModel(
            sceneRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.startEditingScene(scene)

        // Assert
        assertThat(viewModel.uiState.value.editorState).isEqualTo(
            SceneEditorState(
                sceneId = 8L,
                name = "Tavern",
                description = "Warm lights",
                selectedPredefinedTags = setOf("Tavern"),
                customTagsInput = "boss fight",
            )
        )
    }

    @Test
    fun saveSceneEdits_trims_inputs_and_updates_the_repository() = runTest {
        // Arrange
        val repository = FakeSceneRepository()
        val viewModel = ScenesViewModel(
            sceneRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        viewModel.startEditingScene(
            Scene(
                id = 3L,
                name = "Old Tavern",
                description = "Old description",
                tags = listOf("Tavern"),
                soundscapeCount = 0,
            )
        )
        viewModel.updateEditorName("  New Tavern  ")
        viewModel.updateEditorDescription("  Lively inn  ")
        viewModel.togglePredefinedTag("Combat")
        viewModel.updateCustomTagsInput(" boss fight,  night ")

        // Act
        viewModel.saveSceneEdits()
        advanceUntilIdle()

        // Assert
        assertThat(repository.updatedRequests).containsExactly(
            UpdateSceneRequest(
                sceneId = 3L,
                name = "New Tavern",
                description = "Lively inn",
                tags = listOf("Combat", "Tavern", "boss fight", "night"),
            )
        )
        assertThat(viewModel.uiState.value.editorState).isNull()
    }

    @Test
    fun saveSceneEdits_removes_tags_that_are_no_longer_selected() = runTest {
        // Arrange
        val repository = FakeSceneRepository()
        val viewModel = ScenesViewModel(
            sceneRepository = repository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        viewModel.startEditingScene(
            Scene(
                id = 5L,
                name = "Dungeon",
                description = null,
                tags = listOf("Combat", "boss fight"),
                soundscapeCount = 0,
            )
        )
        viewModel.togglePredefinedTag("Combat")
        viewModel.updateCustomTagsInput("")

        // Act
        viewModel.saveSceneEdits()
        advanceUntilIdle()

        // Assert
        assertThat(repository.updatedRequests.single().tags).isEmpty()
    }

    private class FakeSceneRepository : SceneRepository {
        private val scenesFlow = MutableStateFlow<List<Scene>>(emptyList())

        val createdRequests = mutableListOf<CreateSceneRequest>()
        val clonedRequests = mutableListOf<CloneSceneRequest>()
        val updatedRequests = mutableListOf<UpdateSceneRequest>()
        val deletedSceneIds = mutableListOf<Long>()

        fun emitScenes(scenes: List<Scene>) {
            scenesFlow.value = scenes
        }

        override fun observeScenes(): Flow<List<Scene>> = scenesFlow

        override fun observeScene(sceneId: Long): Flow<Scene?> = MutableStateFlow(null)

        override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>> =
            MutableStateFlow(emptyList())

        override fun observeSoundscapesForScene(sceneId: Long): Flow<List<SceneSoundscape>> =
            MutableStateFlow(emptyList())

        override fun observeFxForScene(sceneId: Long): Flow<List<SceneFx>> = MutableStateFlow(emptyList())

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long {
            createdRequests += CreateSceneRequest(name, description, tags)
            return createdRequests.size.toLong()
        }

        override suspend fun cloneScene(sceneId: Long, name: String): Long {
            clonedRequests += CloneSceneRequest(sceneId, name)
            return clonedRequests.size.toLong()
        }

        override suspend fun deleteScene(sceneId: Long) {
            deletedSceneIds += sceneId
        }

        override suspend fun updateScene(
            sceneId: Long,
            name: String,
            description: String?,
            tags: List<String>,
        ) {
            updatedRequests += UpdateSceneRequest(sceneId, name, description, tags)
        }

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

        override suspend fun addFxToScene(sceneId: Long, fxTrackId: Long) = Unit

        override suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>) = Unit

        override suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long) = Unit
    }

    private data class CreateSceneRequest(
        val name: String,
        val description: String?,
        val tags: List<String>,
    )

    private data class CloneSceneRequest(
        val sceneId: Long,
        val name: String,
    )

    private data class UpdateSceneRequest(
        val sceneId: Long,
        val name: String,
        val description: String?,
        val tags: List<String>,
    )
}
