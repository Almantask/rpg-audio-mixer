package com.example.rpgaudiomixer.app.ui.credits

import com.example.rpgaudiomixer.domain.campaign.Campaign
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.library.FxRepository
import com.example.rpgaudiomixer.domain.library.SoundscapeRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrashViewModelTest {

    private val campaignRepository = mockk<CampaignRepository>()
    private val sessionRepository = mockk<SessionRepository>()
    private val sceneRepository = mockk<SceneRepository>()
    private val soundscapeRepository = mockk<SoundscapeRepository>()
    private val fxRepository = mockk<FxRepository>()

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        every { campaignRepository.observeDeleted() } returns flowOf(emptyList())
        every { sessionRepository.observeDeleted() } returns flowOf(emptyList())
        every { sceneRepository.observeDeleted() } returns flowOf(emptyList())
        every { soundscapeRepository.observeDeletedCategories() } returns flowOf(emptyList())
        every { fxRepository.observeDeleted() } returns flowOf(emptyList())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `combines all deleted items from repositories into sorted list`() = runTest {
        val campaign = Campaign(id = 1, name = "C1", deletedAt = 100L)
        val scene = com.example.rpgaudiomixer.domain.scene.Scene(id = 2, name = "S1", deletedAt = 200L)
        
        every { campaignRepository.observeDeleted() } returns flowOf(listOf(campaign))
        every { sceneRepository.observeDeleted() } returns flowOf(listOf(scene))

        val viewModel = TrashViewModel(
            campaignRepository, sessionRepository, sceneRepository, soundscapeRepository, fxRepository
        )

        val state = viewModel.uiState.filter { it.items.isNotEmpty() }.first()
        
        assertThat(state.items).hasSize(2)
        assertThat(state.items[0]).isInstanceOf(TrashItem.SceneItem::class.java) // 200L > 100L
        assertThat(state.items[1]).isInstanceOf(TrashItem.CampaignItem::class.java)
    }

    @Test
    fun `restore calls corresponding repository restore`() = runTest {
        val campaign = Campaign(id = 5, name = "C5", deletedAt = 100L)
        val item = TrashItem.CampaignItem(campaign)
        
        coEvery { campaignRepository.restore(5) } returns Unit

        val viewModel = TrashViewModel(
            campaignRepository, sessionRepository, sceneRepository, soundscapeRepository, fxRepository
        )
        
        viewModel.restore(item)

        coVerify { campaignRepository.restore(5) }
    }
}
