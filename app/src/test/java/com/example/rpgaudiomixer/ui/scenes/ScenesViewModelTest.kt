package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.model.Scene
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
class ScenesViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun createScene_adds_a_trimmed_scene_to_the_success_state() = runTest(mainDispatcherRule.dispatcher) {
        // Arrange
        val repository = FakeSceneRepository()
        val viewModel = ScenesViewModel(repository)

        // Act
        viewModel.createScene(
            name = "  Tavern  ",
            description = "Warm and noisy",
            tags = listOf("social", "indoors"),
        )
        advanceUntilIdle()

        // Assert
        val successState = viewModel.uiState.value as ScenesUiState.Success
        assertThat(successState.scenes).containsExactly(
            Scene(
                id = 1L,
                name = "Tavern",
                description = "Warm and noisy",
                tags = listOf("social", "indoors"),
            ),
        )
    }

    private class FakeSceneRepository : SceneRepository {
        private val scenes = MutableStateFlow<List<Scene>>(emptyList())
        private var nextId = 1L

        override fun observeScenes(): Flow<List<Scene>> = scenes

        override fun observeScene(sceneId: Long): Flow<Scene?> {
            return flowOf(scenes.value.firstOrNull { it.id == sceneId })
        }

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long {
            val id = nextId++
            scenes.value = scenes.value + Scene(id, name, description, tags)
            return id
        }

        override suspend fun deleteScene(sceneId: Long) {
            scenes.value = scenes.value.filterNot { it.id == sceneId }
        }
    }
}
