package com.example.rpgaudiomixer.app.ui.scenes

import com.example.rpgaudiomixer.domain.scene.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScenesViewModelTest {

    private val repository = mockk<SceneRepository>(relaxed = true)
    private lateinit var viewModel: ScenesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { repository.observeAll() } returns flowOf(emptyList())
        viewModel = ScenesViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state eventually becomes Success empty`() = runTest {
        val state = viewModel.uiState.first { it is ScenesUiState.Success }
        assertThat(state).isInstanceOf(ScenesUiState.Success::class.java)
        assertThat((state as ScenesUiState.Success).scenes).isEmpty()
    }

    @Test
    fun `observes all scenes`() = runTest {
        val scenes = listOf(Scene(name = "Scene 1"))
        every { repository.observeAll() } returns flowOf(scenes)
        
        // Re-init
        viewModel = ScenesViewModel(repository)
        
        val state = viewModel.uiState.first { it is ScenesUiState.Success }
        assertThat(state).isInstanceOf(ScenesUiState.Success::class.java)
        assertThat((state as ScenesUiState.Success).scenes).containsExactlyElementsOf(scenes)
    }

    @Test
    fun `createScene calls repository upsert`() = runTest {
        val name = "New Scene"
        val tags = listOf("Forest", "Combat")
        
        viewModel.createScene(name, tags = tags)
        
        coVerify { repository.upsert(match { it.name == name && it.tags == tags }) }
    }

    @Test
    fun `deleteScene calls repository delete`() = runTest {
        val id = 123L
        
        viewModel.deleteScene(id)
        
        coVerify { repository.delete(id) }
    }
}
