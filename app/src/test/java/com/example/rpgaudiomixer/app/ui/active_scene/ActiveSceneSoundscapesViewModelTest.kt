package com.example.rpgaudiomixer.app.ui.active_scene

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.library.SoundscapeCategory
import com.example.rpgaudiomixer.domain.media.CategoryPlayer
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.scene.SceneActiveSoundscape
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import io.mockk.*
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveSceneSoundscapesViewModelTest {

    private val sceneRepository = mockk<SceneRepository>(relaxed = true)
    private val campaignRepository = mockk<CampaignRepository>(relaxed = true)
    private val sessionRepository = mockk<SessionRepository>(relaxed = true)
    private val audioEngine = mockk<SceneAudioEngine>(relaxed = true)
    private val categoryPlayer = mockk<CategoryPlayer>(relaxed = true)
    
    private lateinit var viewModel: ActiveSceneSoundscapesViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { audioEngine.getPlayer(any()) } returns categoryPlayer
        every { categoryPlayer.isPlaying } returns MutableStateFlow(false)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(sceneId: Long = 1L, sessionId: Long = -1L, campaignId: Long = -1L) {
        viewModel = ActiveSceneSoundscapesViewModel(
            sceneRepository,
            campaignRepository,
            sessionRepository,
            audioEngine,
            SavedStateHandle(mapOf(
                "sceneId" to sceneId,
                "sessionId" to sessionId,
                "campaignId" to campaignId
            ))
        )
    }

    @Test
    fun `toggleCategory starts playing if player was stopped`() = runTest {
        val categoryId = 100L
        val soundscape = SceneActiveSoundscape(
            category = SoundscapeCategory(
                id = categoryId, 
                name = "Test", 
                tracks = listOf(
                    com.example.rpgaudiomixer.domain.library.SoundscapeTrack(
                        categoryId = categoryId,
                        name = "Track 1",
                        filePath = "path",
                        intensityLevel = IntensityLevel.I
                    )
                )
            ),
            displayOrder = 0,
            mixVolume = 0.7f,
            intensityLevel = IntensityLevel.I
        )
        
        every { sceneRepository.observeSceneActiveSoundscapes(1L) } returns flowOf(listOf(soundscape))
        createViewModel(1L)
        
        // Trigger state collection
        val job = launch { viewModel.uiState.collect {} }
        viewModel.uiState.first { !it.isLoading }
        assertThat(viewModel.uiState.value.soundscapes).isNotEmpty
        
        viewModel.toggleCategory(categoryId)
        
        verify { categoryPlayer.setMixVolume(0.7f) }
        job.cancel()
    }

    @Test
    fun `setMasterVolume updates audioEngine and state`() = runTest {
        every { sceneRepository.observeSceneActiveSoundscapes(any()) } returns flowOf(emptyList())
        createViewModel()
        
        viewModel.setMasterVolume(0.5f)
        
        assertThat(viewModel.masterVolume.value).isEqualTo(0.5f)
        verify { audioEngine.setMasterVolume(0.5f) }
    }
}
