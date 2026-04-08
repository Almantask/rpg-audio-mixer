package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.model.Scene
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

    private class FakeSceneRepository : SceneRepository {
        private val scenesFlow = MutableStateFlow<List<Scene>>(emptyList())

        val createdRequests = mutableListOf<CreateSceneRequest>()
        val deletedSceneIds = mutableListOf<Long>()

        fun emitScenes(scenes: List<Scene>) {
            scenesFlow.value = scenes
        }

        override fun observeScenes(): Flow<List<Scene>> = scenesFlow

        override fun observeScene(sceneId: Long): Flow<Scene?> = MutableStateFlow(null)

        override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>> =
            MutableStateFlow(emptyList())

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long {
            createdRequests += CreateSceneRequest(name, description, tags)
            return createdRequests.size.toLong()
        }

        override suspend fun deleteScene(sceneId: Long) {
            deletedSceneIds += sceneId
        }

        override suspend fun linkScenesToSession(sessionId: Long, sceneIds: List<Long>) = Unit

        override suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long) = Unit
    }

    private data class CreateSceneRequest(
        val name: String,
        val description: String?,
        val tags: List<String>,
    )
}
