package com.example.rpgaudiomixer.app.ui.sessions

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.domain.scene.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
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
class SessionScenesViewModelTest {

    private val sessionRepository = mockk<SessionRepository>(relaxed = true)
    private val sceneRepository = mockk<SceneRepository>(relaxed = true)
    private lateinit var viewModel: SessionScenesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val sessionId = 123L

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { sessionRepository.observeScenesBySession(sessionId) } returns flowOf(emptyList())
        every { sceneRepository.observeAll() } returns flowOf(emptyList())
        val savedStateHandle = SavedStateHandle(mapOf("sessionId" to sessionId))
        viewModel = SessionScenesViewModel(sessionRepository, sceneRepository, savedStateHandle)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state becomes Success`() = runTest {
        val state = viewModel.uiState.first { it is SessionScenesUiState.Success }
        assertThat(state).isInstanceOf(SessionScenesUiState.Success::class.java)
    }

    @Test
    fun `linkScene calls sessionRepository linkScene`() = runTest {
        val sceneId = 456L
        
        viewModel.linkScene(sceneId)
        
        coVerify { sessionRepository.linkScene(sessionId, sceneId) }
    }

    @Test
    fun `unlinkScene calls sessionRepository unlinkScene`() = runTest {
        val sceneId = 456L
        
        viewModel.unlinkScene(sceneId)
        
        coVerify { sessionRepository.unlinkScene(sessionId, sceneId) }
    }
}
