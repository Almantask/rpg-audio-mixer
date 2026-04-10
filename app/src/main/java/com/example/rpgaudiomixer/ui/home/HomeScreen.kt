package com.example.rpgaudiomixer.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class HomeResumeJourneyUiState(
    val sessionId: Long,
    val sceneId: Long,
    val sceneName: String,
    val sceneDescription: String?,
)

data class HomeTrackHighlightUiState(
    val trackName: String,
    val categoryName: String,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val activeCampaign: Campaign? = null,
    val resumeJourney: HomeResumeJourneyUiState? = null,
    val topAtmosphere: HomeTrackHighlightUiState? = null,
    val legendaryAction: HomeTrackHighlightUiState? = null,
    val emptyMessage: String? = null,
    val errorMessage: String? = null,
)

@Composable
fun HomeRoute(
    onOpenCampaign: (Long) -> Unit,
    onOpenResumeScene: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onOpenCampaign = onOpenCampaign,
        onOpenResumeScene = onOpenResumeScene,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onOpenCampaign: (Long) -> Unit,
    onOpenResumeScene: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var errorMessage by rememberSaveable(uiState.errorMessage) { mutableStateOf(uiState.errorMessage) }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.activeCampaign == null) {
            EmptyStateView(
                modifier = Modifier.align(Alignment.Center),
                illustration = Icons.Default.AutoStories,
                title = uiState.emptyMessage ?: "Create a campaign to begin your next journey.",
                actionLabel = "Visit Campaigns",
                onActionClick = {},
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionLabel(text = "Active Campaign")
                    ActiveCampaignCard(
                        campaign = uiState.activeCampaign,
                        onOpenCampaign = { onOpenCampaign(uiState.activeCampaign.id) },
                    )
                }
                uiState.resumeJourney?.let { resumeJourney ->
                    item {
                        SectionLabel(text = "Resume Journey")
                        ResumeJourneyCard(
                            resumeJourney = resumeJourney,
                            onOpenResumeScene = { onOpenResumeScene(resumeJourney.sceneId) },
                        )
                    }
                }
                item {
                    SectionLabel(text = "Top Atmosphere")
                    HighlightCard(
                        title = uiState.topAtmosphere?.trackName ?: "No atmosphere played yet",
                        subtitle = uiState.topAtmosphere?.categoryName ?: "Open a scene to build your legend",
                        icon = Icons.Default.LocalFireDepartment,
                    )
                }
                item {
                    SectionLabel(text = "Legendary Action")
                    HighlightCard(
                        title = uiState.legendaryAction?.trackName ?: "No legendary action played yet",
                        subtitle = uiState.legendaryAction?.categoryName ?: "Trigger an effect to see it here",
                        icon = Icons.Default.Bolt,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        ErrorDialog(
            message = errorMessage,
            onDismiss = { errorMessage = null },
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        modifier = Modifier.padding(bottom = 8.dp),
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun ActiveCampaignCard(
    campaign: Campaign,
    onOpenCampaign: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (campaign.coverArtUri != null) {
                AsyncImage(
                    model = campaign.coverArtUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = campaign.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Button(onClick = onOpenCampaign) {
                    Text(text = "Enter Domain")
                }
            }
        }
    }
}

@Composable
private fun ResumeJourneyCard(
    resumeJourney: HomeResumeJourneyUiState,
    onOpenResumeScene: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = resumeJourney.sceneName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            resumeJourney.sceneDescription?.takeIf(String::isNotBlank)?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(onClick = onOpenResumeScene) {
                Text(text = "Enter")
            }
        }
    }
}

@Composable
private fun HighlightCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository,
) : ViewModel() {
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    internal constructor(
        campaignRepository: CampaignRepository,
        sessionRepository: SessionRepository,
        sceneRepository: SceneRepository,
        soundscapeRepository: SoundscapeRepository,
        fxRepository: FxRepository,
        mainDispatcher: CoroutineDispatcher,
    ) : this(
        campaignRepository = campaignRepository,
        sessionRepository = sessionRepository,
        sceneRepository = sceneRepository,
        soundscapeRepository = soundscapeRepository,
        fxRepository = fxRepository,
    ) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val activeCampaignFlow = campaignRepository.observeCampaigns().flatMapLatest { campaigns ->
            flowOf(campaigns.maxWithOrNull(compareBy<Campaign> { it.lastPlayedAt }.thenBy { it.id }))
        }
        val sessionsFlow = activeCampaignFlow.flatMapLatest { activeCampaign ->
            if (activeCampaign == null) {
                flowOf(emptyList())
            } else {
                sessionRepository.observeSessions(activeCampaign.id)
            }
        }
        val resumeJourneyFlow = sessionsFlow.flatMapLatest { sessions ->
            val lastOpenedSession = sessions
                .filter { it.lastOpenedSceneId != null && it.lastOpenedAt != null }
                .maxWithOrNull(compareBy<Session> { it.lastOpenedAt ?: Long.MIN_VALUE }.thenBy { it.id })
            val sceneId = lastOpenedSession?.lastOpenedSceneId
            if (lastOpenedSession == null || sceneId == null) {
                flowOf(null)
            } else {
                sceneRepository.observeScene(sceneId).flatMapLatest { scene ->
                    flowOf(
                        scene?.let {
                            HomeResumeJourneyUiState(
                                sessionId = lastOpenedSession.id,
                                sceneId = it.id,
                                sceneName = it.name,
                                sceneDescription = it.description,
                            )
                        }
                    )
                }
            }
        }
        val topAtmosphereFlow = soundscapeRepository.observeCategories().flatMapLatest { categories ->
            categories.combineCategoryTrackFlows(soundscapeRepository).flatMapLatest { tracksByCategory ->
                flowOf(
                    tracksByCategory.entries
                        .flatMap { (categoryId, tracks) ->
                            val categoryName = categories.firstOrNull { it.id == categoryId }?.name ?: return@flatMap emptyList()
                            tracks.map { track ->
                                HomeTrackHighlightUiState(
                                    trackName = track.name,
                                    categoryName = categoryName,
                                ) to track.playCount
                            }
                        }
                        .maxWithOrNull(compareBy<Pair<HomeTrackHighlightUiState, Int>> { it.second }.thenBy { it.first.trackName })
                        ?.first
                )
            }
        }
        val legendaryActionFlow = fxRepository.observeFxTracks().flatMapLatest { tracks ->
            flowOf(
                tracks.maxWithOrNull(compareBy<FxTrack> { it.playCount }.thenBy { it.name })?.let { track ->
                    HomeTrackHighlightUiState(
                        trackName = track.name,
                        categoryName = track.tags.firstOrNull() ?: "FX",
                    )
                }
            )
        }

        viewModelScope.launch(mainDispatcher) {
            combine(
                activeCampaignFlow,
                resumeJourneyFlow,
                topAtmosphereFlow,
                legendaryActionFlow,
            ) { activeCampaign, resumeJourney, topAtmosphere, legendaryAction ->
                HomeUiState(
                    isLoading = false,
                    activeCampaign = activeCampaign,
                    resumeJourney = resumeJourney,
                    topAtmosphere = topAtmosphere,
                    legendaryAction = legendaryAction,
                    emptyMessage = if (activeCampaign == null) {
                        "Create a campaign to begin your next journey."
                    } else {
                        null
                    },
                )
            }
                .catch { throwable ->
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load home screen.",
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
}

private fun List<SoundscapeCategory>.combineCategoryTrackFlows(
    soundscapeRepository: SoundscapeRepository,
) = if (isEmpty()) {
    flowOf(emptyMap())
} else {
    val categories = this
    combine(categories.map { category ->
        soundscapeRepository.observeTracks(category.id)
    }) { trackLists ->
        categories.mapIndexed { index, category ->
            category.id to (trackLists[index] as List<SoundscapeTrack>)
        }.toMap()
    }
}
