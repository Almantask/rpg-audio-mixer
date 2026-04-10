package com.example.rpgaudiomixer.ui.home

import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HomeViewModelTest {

    @Test
    fun init_exposes_empty_state_when_no_campaigns_exist() = runTest {
        // Arrange
        val viewModel = buildViewModel(
            testDispatcher = StandardTestDispatcher(testScheduler),
        )

        // Act
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(
            HomeUiState(
                isLoading = false,
                emptyMessage = "Create a campaign to begin your next journey.",
            )
        )
    }

    @Test
    fun init_selects_the_active_campaign_resume_scene_and_top_tracks() = runTest {
        // Arrange
        val campaignRepository = FakeCampaignRepository().apply {
            campaignsFlow.value = listOf(
                Campaign(id = 1L, name = "Lost Mine", coverArtUri = null, lastPlayedAt = 100L),
                Campaign(id = 2L, name = "Curse of Strahd", coverArtUri = "content://castle", lastPlayedAt = 500L),
            )
        }
        val sessionRepository = FakeSessionRepository().apply {
            sessionsByCampaign[2L] = MutableStateFlow(
                listOf(
                    Session(
                        id = 10L,
                        campaignId = 2L,
                        name = "Castle Ravenloft",
                        date = 200L,
                        coverArtUri = null,
                        sceneCount = 3,
                        lastOpenedSceneId = 99L,
                        lastOpenedAt = 700L,
                    )
                )
            )
        }
        val sceneRepository = FakeSceneRepository().apply {
            scenesById[99L] = MutableStateFlow(
                Scene(
                    id = 99L,
                    name = "The Foyer",
                    description = "A cold marble hall.",
                    tags = emptyList(),
                    soundscapeCount = 1,
                )
            )
        }
        val soundscapeRepository = FakeSoundscapeRepository().apply {
            categoriesFlow.value = listOf(
                SoundscapeCategory(
                    id = 20L,
                    name = "Tavern",
                    iconResId = null,
                    themeLabel = null,
                    levelOneTrackCount = 1,
                    levelTwoTrackCount = 0,
                    levelThreeTrackCount = 0,
                ),
                SoundscapeCategory(
                    id = 30L,
                    name = "Weather",
                    iconResId = null,
                    themeLabel = null,
                    levelOneTrackCount = 1,
                    levelTwoTrackCount = 0,
                    levelThreeTrackCount = 0,
                ),
            )
            tracksByCategory[20L] = MutableStateFlow(
                listOf(soundscapeTrack(id = 1L, categoryId = 20L, name = "Tavern Warmth", playCount = 12))
            )
            tracksByCategory[30L] = MutableStateFlow(
                listOf(soundscapeTrack(id = 2L, categoryId = 30L, name = "Storm Front", playCount = 4))
            )
        }
        val fxRepository = FakeFxRepository().apply {
            tracksFlow.value = listOf(
                fxTrack(id = 8L, name = "Thunder Crack", playCount = 9, tags = listOf("Weather")),
                fxTrack(id = 9L, name = "Sword Clash", playCount = 2, tags = listOf("Combat")),
            )
        }

        // Act
        val viewModel = buildViewModel(
            campaignRepository = campaignRepository,
            sessionRepository = sessionRepository,
            sceneRepository = sceneRepository,
            soundscapeRepository = soundscapeRepository,
            fxRepository = fxRepository,
            testDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value).isEqualTo(
            HomeUiState(
                isLoading = false,
                activeCampaign = Campaign(id = 2L, name = "Curse of Strahd", coverArtUri = "content://castle", lastPlayedAt = 500L),
                resumeJourney = HomeResumeJourneyUiState(
                    sessionId = 10L,
                    sceneId = 99L,
                    sceneName = "The Foyer",
                    sceneDescription = "A cold marble hall.",
                ),
                topAtmosphere = HomeTrackHighlightUiState(
                    trackName = "Tavern Warmth",
                    categoryName = "Tavern",
                ),
                legendaryAction = HomeTrackHighlightUiState(
                    trackName = "Thunder Crack",
                    categoryName = "Weather",
                ),
            )
        )
    }

    @Test
    fun init_hides_resume_journey_when_the_active_campaign_has_no_opened_scene() = runTest {
        // Arrange
        val campaignRepository = FakeCampaignRepository().apply {
            campaignsFlow.value = listOf(
                Campaign(id = 2L, name = "Curse of Strahd", coverArtUri = null, lastPlayedAt = 500L),
            )
        }
        val sessionRepository = FakeSessionRepository().apply {
            sessionsByCampaign[2L] = MutableStateFlow(
                listOf(
                    Session(
                        id = 10L,
                        campaignId = 2L,
                        name = "Castle Ravenloft",
                        date = 200L,
                        coverArtUri = null,
                        sceneCount = 3,
                    )
                )
            )
        }

        // Act
        val viewModel = buildViewModel(
            campaignRepository = campaignRepository,
            sessionRepository = sessionRepository,
            testDispatcher = StandardTestDispatcher(testScheduler),
        )
        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.resumeJourney).isNull()
    }

    private fun buildViewModel(
        campaignRepository: CampaignRepository = FakeCampaignRepository(),
        sessionRepository: SessionRepository = FakeSessionRepository(),
        sceneRepository: SceneRepository = FakeSceneRepository(),
        soundscapeRepository: SoundscapeRepository = FakeSoundscapeRepository(),
        fxRepository: FxRepository = FakeFxRepository(),
        testDispatcher: StandardTestDispatcher = StandardTestDispatcher(),
    ): HomeViewModel = HomeViewModel(
        campaignRepository = campaignRepository,
        sessionRepository = sessionRepository,
        sceneRepository = sceneRepository,
        soundscapeRepository = soundscapeRepository,
        fxRepository = fxRepository,
        mainDispatcher = testDispatcher,
    )

    private class FakeCampaignRepository : CampaignRepository {
        val campaignsFlow = MutableStateFlow<List<Campaign>>(emptyList())

        override fun observeCampaigns(): Flow<List<Campaign>> = campaignsFlow

        override fun observeCampaign(campaignId: Long): Flow<Campaign?> =
            MutableStateFlow(campaignsFlow.value.firstOrNull { it.id == campaignId })

        override suspend fun createCampaign(name: String, coverArtUri: String?): Long = 0L

        override suspend fun deleteCampaign(campaignId: Long) = Unit
    }

    private class FakeSessionRepository : SessionRepository {
        val sessionsByCampaign = mutableMapOf<Long, MutableStateFlow<List<Session>>>()

        override fun observeSessions(campaignId: Long): Flow<List<Session>> =
            sessionsByCampaign.getOrPut(campaignId) { MutableStateFlow(emptyList()) }

        override fun observeSession(sessionId: Long): Flow<Session?> = MutableStateFlow(null)

        override suspend fun createSession(
            campaignId: Long,
            name: String,
            date: Long,
            coverArtUri: String?,
        ): Long = 0L

        override suspend fun deleteSession(sessionId: Long) = Unit

        override suspend fun recordOpenedScene(sessionId: Long, sceneId: Long) = Unit
    }

    private class FakeSceneRepository : SceneRepository {
        val scenesById = mutableMapOf<Long, MutableStateFlow<Scene?>>()

        override fun observeScenes(): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeScene(sceneId: Long): Flow<Scene?> =
            scenesById.getOrPut(sceneId) { MutableStateFlow(null) }

        override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>> = MutableStateFlow(emptyList())

        override fun observeSoundscapesForScene(sceneId: Long): Flow<List<SceneSoundscape>> = MutableStateFlow(emptyList())

        override fun observeFxForScene(sceneId: Long): Flow<List<SceneFx>> = MutableStateFlow(emptyList())

        override suspend fun createScene(name: String, description: String?, tags: List<String>): Long = 0L

        override suspend fun cloneScene(sceneId: Long, name: String): Long = 0L

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

        override suspend fun addFxToScene(sceneId: Long, fxTrackId: Long) = Unit

        override suspend fun reorderFx(sceneId: Long, orderedFxTrackIds: List<Long>) = Unit

        override suspend fun removeFxFromScene(sceneId: Long, fxTrackId: Long) = Unit
    }

    private class FakeSoundscapeRepository : SoundscapeRepository {
        val categoriesFlow = MutableStateFlow<List<SoundscapeCategory>>(emptyList())
        val tracksByCategory = mutableMapOf<Long, MutableStateFlow<List<SoundscapeTrack>>>()

        override fun observeCategories(): Flow<List<SoundscapeCategory>> = categoriesFlow

        override fun observeCategory(categoryId: Long): Flow<SoundscapeCategory?> = MutableStateFlow(null)

        override fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>> =
            tracksByCategory.getOrPut(categoryId) { MutableStateFlow(emptyList()) }

        override fun observeHasDemoSoundscapes(): Flow<Boolean> = MutableStateFlow(false)

        override suspend fun createCategory(name: String): Long = 0L

        override suspend fun deleteCategory(categoryId: Long) = Unit

        override suspend fun importTrack(categoryId: Long, sourceUri: String): SoundscapeTrack =
            soundscapeTrack(id = 0L, categoryId = categoryId, name = sourceUri)

        override suspend fun saveTracks(categoryId: Long, tracks: List<SoundscapeTrack>) = Unit

        override suspend fun seedDemoSoundscapes() = Unit

        override suspend fun incrementTrackPlayCount(trackId: Long) = Unit
    }

    private class FakeFxRepository : FxRepository {
        val tracksFlow = MutableStateFlow<List<FxTrack>>(emptyList())

        override fun observeFxTracks(): Flow<List<FxTrack>> = tracksFlow

        override fun searchFxTracks(query: String): Flow<List<FxTrack>> = tracksFlow

        override fun observeHasDemoFxTracks(): Flow<Boolean> = MutableStateFlow(false)

        override suspend fun importFxTrack(sourceUri: String): FxTrack = fxTrack(id = 0L, name = sourceUri)

        override suspend fun updateFxTrack(track: FxTrack) = Unit

        override suspend fun softDeleteFxTrack(trackId: Long) = Unit

        override suspend fun seedDemoFxTracks() = Unit

        override suspend fun incrementPlayCount(trackId: Long) = Unit
    }

    private fun soundscapeTrack(
        id: Long,
        categoryId: Long,
        name: String,
        playCount: Int = 0,
    ) = SoundscapeTrack(
        id = id,
        categoryId = categoryId,
        name = name,
        filePath = "/tracks/$name.mp3",
        intensityLevel = IntensityLevel.I,
        mixVolume = 1f,
        playCount = playCount,
    )

    private fun fxTrack(
        id: Long,
        name: String,
        playCount: Int = 0,
        tags: List<String> = emptyList(),
    ) = FxTrack(
        id = id,
        name = name,
        filePath = "/fx/$name.mp3",
        tags = tags,
        durationMs = 1000L,
        playCount = playCount,
        isDemo = false,
    )
}
