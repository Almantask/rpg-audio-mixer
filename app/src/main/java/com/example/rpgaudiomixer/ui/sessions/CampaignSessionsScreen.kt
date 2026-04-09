package com.example.rpgaudiomixer.ui.sessions

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SessionArtwork
import com.example.rpgaudiomixer.app.components.SessionCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.navigation.AppRoute
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class CampaignSessionsUiState(
    val isLoading: Boolean = true,
    val campaign: Campaign? = null,
    val sessions: List<Session> = emptyList(),
    val errorMessage: String? = null,
)

@Composable
fun CampaignSessionsRoute(
    onOpenSession: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CampaignSessionsScreen(
        uiState = uiState,
        onCreateSession = viewModel::createSession,
        onDeleteSession = viewModel::deleteSession,
        onOpenSession = onOpenSession,
        modifier = modifier,
    )
}

@Composable
fun CampaignSessionsScreen(
    uiState: CampaignSessionsUiState,
    onCreateSession: (String, Long, String?) -> Unit,
    onDeleteSession: (Long) -> Unit,
    onOpenSession: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable(uiState.errorMessage) { mutableStateOf(uiState.errorMessage) }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.sessions.isEmpty()) {
            EmptyStateView(
                modifier = Modifier.align(Alignment.Center),
                illustration = Icons.Default.Description,
                title = "No sessions yet",
                actionLabel = "Add New Session",
                onActionClick = { showCreateDialog = true },
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    CampaignHeroBanner(campaign = uiState.campaign)
                }
                items(items = uiState.sessions, key = Session::id) { session ->
                    SwipeToDeleteContainer(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onDelete = { onDeleteSession(session.id) },
                    ) {
                        SessionCard(
                            session = session,
                            onOpenSession = { onOpenSession(session.id) },
                        )
                    }
                }
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = { showCreateDialog = true },
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = "Add New Session")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (showCreateDialog) {
            CreateSessionDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, date, coverArtUri ->
                    onCreateSession(name, date, coverArtUri)
                    showCreateDialog = false
                },
            )
        }

        ErrorDialog(
            message = errorMessage,
            onDismiss = { errorMessage = null },
        )
    }
}

@Composable
private fun CampaignHeroBanner(
    campaign: Campaign?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .aspectRatio(16f / 7f),
    ) {
        if (campaign?.coverArtUri != null) {
            AsyncImage(
                model = campaign.coverArtUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            SessionArtwork(
                coverArtUri = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            text = campaign?.name ?: "Campaign",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Long, String?) -> Unit,
) {
    var sessionName by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var coverArtUri by rememberSaveable { mutableStateOf<String?>(null) }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            coverArtUri = uri?.toString()
        },
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Create a New Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    label = { Text(text = "Session name") },
                    singleLine = true,
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                ) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = if (coverArtUri == null) "Choose Cover Art" else "Cover Art Selected")
                }
                Text(
                    text = "Session date: ${selectedDate.asReadableDate()}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = { selectedDate = System.currentTimeMillis() }) {
                    Text(text = "Use Today")
                }
                if (coverArtUri != null) {
                    SessionArtwork(
                        coverArtUri = coverArtUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(sessionName, selectedDate, coverArtUri) },
                enabled = sessionName.isNotBlank(),
            ) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}

private fun Long.asReadableDate(): String = this.toDisplayDate()

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
) : ViewModel() {
    private val campaignId: Long = requireNotNull(savedStateHandle[AppRoute.CAMPAIGN_ID_ARG])
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    internal constructor(
        campaignId: Long,
        campaignRepository: CampaignRepository,
        sessionRepository: SessionRepository,
        mainDispatcher: CoroutineDispatcher,
    ) : this(
        savedStateHandle = SavedStateHandle(mapOf(AppRoute.CAMPAIGN_ID_ARG to campaignId)),
        campaignRepository = campaignRepository,
        sessionRepository = sessionRepository,
    ) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(CampaignSessionsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            combine(
                campaignRepository.observeCampaign(campaignId),
                sessionRepository.observeSessions(campaignId),
            ) { campaign, sessions ->
                CampaignSessionsUiState(
                    isLoading = false,
                    campaign = campaign,
                    sessions = sessions,
                )
            }
                .catch { throwable ->
                    _uiState.value = CampaignSessionsUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load sessions.",
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun createSession(
        name: String,
        date: Long,
        coverArtUri: String?,
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return
        }
        viewModelScope.launch(mainDispatcher) {
            sessionRepository.createSession(
                campaignId = campaignId,
                name = trimmedName,
                date = date,
                coverArtUri = coverArtUri,
            )
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch(mainDispatcher) {
            sessionRepository.deleteSession(sessionId)
        }
    }
}
