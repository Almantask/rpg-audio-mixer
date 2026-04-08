package com.example.rpgaudiomixer.ui.sessionscenes

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SessionScenesViewModelTest {

    @Test
    fun init_exposes_session_details_linked_scenes_and_available_scenes() = runTest {
        // Arrange
        val sessionRepository = FakeSessionRepository()
        val sceneRepository = FakeSceneRepository()
        sessionRepository.sessionFlow.value = Session(7L, 3L, "Session 1", 200L, null, 1)
        sceneRepository.linkedScenesFlow.value = listOf(
            Scene(2L, "Tavern", null, listOf("tavern"), 0),
        )
        sceneRepository.availableScenesFlow.value = listOf(
            Scene(3L, "Forest", null, listOf("forest"), 0),
        )

        // Act
        val viewModel = SessionScenesViewModel(
            sessionId = 7L,
            sessionRepository = sessionRepository,
            sceneRepository = sceneRepository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(
            SessionScenesUiState(
                isLoading = false,
                session = Session(7L, 3L, "Session 1", 200L, null, 1),
                linkedScenes = listOf(Scene(2L, "Tavern", null, listOf("tavern"), 0)),
                availableScenes = listOf(Scene(3L, "Forest", null, listOf("forest"), 0)),
            )
        )
    }

    @Test
    fun showImportPicker_sets_the_picker_visibility_to_true() = runTest {
        // Arrange
        val viewModel = SessionScenesViewModel(
            sessionId = 7L,
            sessionRepository = FakeSessionRepository(),
            sceneRepository = FakeSceneRepository(),
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.showImportPicker()
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.isImportPickerVisible).isTrue()
    }

    @Test
    fun importScenes_links_the_selected_scene_ids_and_closes_the_picker() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository()
        val viewModel = SessionScenesViewModel(
            sessionId = 7L,
            sessionRepository = FakeSessionRepository(),
            sceneRepository = sceneRepository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )
        viewModel.showImportPicker()
        advanceUntilIdle()

        // Act
        viewModel.importScenes(listOf(2L, 3L))
        advanceUntilIdle()

        // Assert
        assertThat(sceneRepository.linkRequests).containsExactly(7L to listOf(2L, 3L))
        assertThat(viewModel.uiState.value.isImportPickerVisible).isFalse()
    }

    @Test
    fun unlinkScene_delegates_to_the_repository() = runTest {
        // Arrange
        val sceneRepository = FakeSceneRepository()
        val viewModel = SessionScenesViewModel(
            sessionId = 7L,
            sessionRepository = FakeSessionRepository(),
            sceneRepository = sceneRepository,
            mainDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        viewModel.unlinkScene(5L)
        advanceUntilIdle()

        // Assert
        assertThat(sceneRepository.unlinkRequests).containsExactly(7L to 5L)
    }

    private class FakeSessionRepository : SessionRepository {
        val sessionFlow = MutableStateFlow<Session?>(null)

        override fun observeSessions(campaignId: Long): Flow<List<Session>> = MutableStateFlow(emptyList())

        override fun observeSession(sessionId: Long): Flow<Session?> = sessionFlow

        override suspend fun createSession(
            campaignId: Long,
            name: String,
            date: Long,
            coverArtUri: String?,
        ): Long = 0L

        override suspend fun deleteSession(sessionId: Long) = Unit
    }

    private class FakeSceneRepository : SceneRepository {
        val linkedScenesFlow = MutableStateFlow<List<Scene>>(emptyList())
        val availableScenesFlow = MutableStateFlow<List<Scene>>(emptyList())
        val linkRequests = mutableListOf<Pair<Long, List<Long>>>()
        val unlinkRequests = mutableListOf<Pair<Long, Long>>()

        override fun observeScenes(): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeScene(sceneId: Long): Flow<Scene?> = MutableStateFlow(null)

        override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> = linkedScenesFlow

        override fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>> =
            availableScenesFlow

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long = 0L

        override suspend fun deleteScene(sceneId: Long) = Unit

        override suspend fun linkScenesToSession(sessionId: Long, sceneIds: List<Long>) {
            linkRequests += sessionId to sceneIds
        }

        override suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long) {
            unlinkRequests += sessionId to sceneId
        }
    }
}
