package com.example.rpgaudiomixer.ui.sessions

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.campaign.CampaignRepository
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.domain.session.SessionRepository
import com.example.rpgaudiomixer.domain.trash.SessionTrashRepository
import com.example.rpgaudiomixer.domain.trash.TrashVaultRepository
import com.example.rpgaudiomixer.ui.campaigns.CampaignPhotoPickerMode
import com.example.rpgaudiomixer.ui.campaigns.CampaignsTestTags
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SessionsTestTags {
    const val SCREEN = "Screen_CampaignSessions"
    const val LIST = "Sessions_List"
    const val HERO = "Sessions_Hero"
    const val EMPTY_ILLUSTRATION = CampaignsTestTags.EMPTY_ILLUSTRATION
    const val NEW_BUTTON = "Sessions_New_Button"
    const val CREATE_DIALOG = "Sessions_Create_Dialog"
    const val NAME_INPUT = "Sessions_Create_Name"
    const val COVER_ART_PICKER = CampaignsTestTags.COVER_ART_PICKER
    const val COVER_ART_PREVIEW = CampaignsTestTags.COVER_ART_PREVIEW

    fun card(name: String): String = "Sessions_Card_${name.asTagSuffix()}"
}

data class CampaignSessionsUiState(
    val isLoading: Boolean = true,
    val campaign: Campaign? = null,
    val sessions: List<Session> = emptyList(),
    val showCreateDialog: Boolean = false,
    val draftName: String = "",
    val draftCoverArtUri: String? = null,
    val errorMessage: String? = null,
)

@Composable
fun CampaignSessionsRoute(
    onOpenSessionScenes: (Long) -> Unit,
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        viewModel.onCoverArtPicked(uri?.toString())
    }

    CampaignSessionsScreen(
        uiState = uiState,
        onOpenCreateDialog = viewModel::openCreateDialog,
        onDraftNameChange = viewModel::updateDraftName,
        onConfirmCreate = viewModel::confirmCreateSession,
        onDismissCreate = viewModel::dismissCreateDialog,
        onPickCoverArt = {
            if (viewModel.useSystemPhotoPicker) {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            }
        },
        onDeleteSession = viewModel::deleteSession,
        onOpenSession = { session -> onOpenSessionScenes(session.id) },
        onDismissError = viewModel::clearError,
    )
}

@Composable
fun CampaignSessionsScreen(
    uiState: CampaignSessionsUiState,
    onOpenCreateDialog: () -> Unit,
    onDraftNameChange: (String) -> Unit,
    onConfirmCreate: () -> Unit,
    onDismissCreate: () -> Unit,
    onPickCoverArt: () -> Unit,
    onDeleteSession: (Session) -> Unit,
    onOpenSession: (Session) -> Unit,
    onDismissError: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SessionsTestTags.SCREEN),
    ) {
        if (uiState.sessions.isEmpty()) {
            SessionsEmptyState(onCreateSession = onOpenCreateDialog)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag(SessionsTestTags.LIST),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    HeroBanner(campaign = uiState.campaign)
                }
                items(uiState.sessions, key = Session::id) { session ->
                    SwipeToDeleteSessionContainer(
                        session = session,
                        onDeleteSession = onDeleteSession,
                    ) {
                        SessionCard(
                            session = session,
                            onOpenSession = onOpenSession,
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding()
                .testTag(SessionsTestTags.NEW_BUTTON),
            onClick = onOpenCreateDialog,
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("Add New Session")
        }

        if (uiState.showCreateDialog) {
            AlertDialog(
                modifier = Modifier.testTag(SessionsTestTags.CREATE_DIALOG),
                onDismissRequest = onDismissCreate,
                title = { Text("Add New Session") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(SessionsTestTags.NAME_INPUT),
                            value = uiState.draftName,
                            onValueChange = onDraftNameChange,
                            singleLine = true,
                            label = { Text("Session name") },
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(132.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(20.dp),
                                )
                                .clickable(onClick = onPickCoverArt)
                                .padding(16.dp)
                                .testTag(SessionsTestTags.COVER_ART_PICKER),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (uiState.draftCoverArtUri == null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Image, contentDescription = null)
                                    Text("Tap to choose cover art")
                                }
                            } else {
                                Text(
                                    modifier = Modifier.testTag(SessionsTestTags.COVER_ART_PREVIEW),
                                    text = "Cover art selected",
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onConfirmCreate) { Text("Create") }
                },
                dismissButton = {
                    TextButton(onClick = onDismissCreate) { Text("Cancel") }
                },
            )
        }

        ErrorDialog(message = uiState.errorMessage, onDismiss = onDismissError)
    }
}

@Composable
private fun SessionsEmptyState(
    onCreateSession: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(28.dp),
                )
                .testTag(SessionsTestTags.EMPTY_ILLUSTRATION),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.CollectionsBookmark,
                contentDescription = null,
                tint = ArcanumGold,
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "No sessions yet",
            fontWeight = FontWeight.Bold,
        )
        FilledTonalButton(
            modifier = Modifier.padding(top = 20.dp),
            onClick = onCreateSession,
        ) {
            Text("Add New Session")
        }
    }
}

@Composable
private fun HeroBanner(campaign: Campaign?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(164.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(28.dp),
            )
            .testTag(SessionsTestTags.HERO),
        contentAlignment = Alignment.BottomStart,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = campaign?.name.orEmpty(),
                color = ArcanumGold,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text("Gather your scenes for the next play night.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteSessionContainer(
    session: Session,
    onDeleteSession: (Session) -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteSession(session)
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text("Move to Trash", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        content = content,
    )
}

@Composable
private fun SessionCard(
    session: Session,
    onOpenSession: (Session) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(SessionsTestTags.card(session.name))
            .clickable { onOpenSession(session) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(18.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(if (session.coverArtUri == null) "No Art" else "Art")
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = session.name,
                    color = ArcanumGold,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text("${session.dateMillis.toReadableDate()} • ${session.sceneCount} scenes")
            }
        }
    }
}

@HiltViewModel
class CampaignSessionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    campaignRepository: CampaignRepository,
    private val sessionRepository: SessionRepository,
    private val sessionCoverArtSelectionRepository: SessionCoverArtSelectionRepository,
    private val sessionTrashRepository: SessionTrashRepository,
    private val trashVaultRepository: TrashVaultRepository,
    photoPickerMode: CampaignPhotoPickerMode,
) : ViewModel() {
    private val campaignId = requireNotNull(savedStateHandle.get<String>("campaignId")) {
        "Navigation argument 'campaignId' is missing."
    }.toLongOrNull() ?: error("Navigation argument 'campaignId' must be a valid numeric value.")

    private val draftState = MutableStateFlow(SessionDraft())
    private val _uiState = MutableStateFlow(CampaignSessionsUiState())
    val uiState: StateFlow<CampaignSessionsUiState> = _uiState.asStateFlow()
    val useSystemPhotoPicker: Boolean = photoPickerMode.useSystemPhotoPicker

    init {
        viewModelScope.launch {
            combine(
                campaignRepository.observeCampaign(campaignId),
                sessionRepository.observeSessions(campaignId),
                draftState,
                sessionCoverArtSelectionRepository.selectedCoverArtUri,
            ) { campaign, sessions, draft, selectedCoverArtUri ->
                CampaignSessionsUiState(
                    isLoading = false,
                    campaign = campaign,
                    sessions = sessions,
                    showCreateDialog = draft.isOpen,
                    draftName = draft.name,
                    draftCoverArtUri = selectedCoverArtUri ?: draft.coverArtUri,
                    errorMessage = draft.errorMessage,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun openCreateDialog() {
        sessionCoverArtSelectionRepository.reset()
        draftState.value = SessionDraft(isOpen = true)
    }

    fun dismissCreateDialog() {
        sessionCoverArtSelectionRepository.reset()
        draftState.value = SessionDraft()
    }

    fun updateDraftName(name: String) {
        draftState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onCoverArtPicked(uri: String?) {
        sessionCoverArtSelectionRepository.updateSelectedCoverArt(uri)
        draftState.update { it.copy(coverArtUri = uri) }
    }

    fun confirmCreateSession() {
        val draft = draftState.value
        if (draft.name.isBlank()) {
            draftState.update { it.copy(errorMessage = "Every session needs a name.") }
            return
        }

        viewModelScope.launch {
            sessionRepository.upsertSession(
                Session(
                    campaignId = campaignId,
                    name = draft.name.trim(),
                    coverArtUri = sessionCoverArtSelectionRepository.selectedCoverArtUri.value ?: draft.coverArtUri,
                ),
            )
            dismissCreateDialog()
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            trashVaultRepository.trashSession(session.id)
            sessionRepository.deleteSession(session.id)
            sessionTrashRepository.recordDeletedSession(session.name)
        }
    }

    fun clearError() {
        draftState.update { it.copy(errorMessage = null) }
    }
}

private data class SessionDraft(
    val isOpen: Boolean = false,
    val name: String = "",
    val coverArtUri: String? = null,
    val errorMessage: String? = null,
)

private val sessionDateFormatter = ThreadLocal.withInitial {
    SimpleDateFormat("MMM d, yyyy", Locale.US)
}

private fun Long.toReadableDate(): String = sessionDateFormatter.get().format(Date(this))

private fun String.asTagSuffix(): String = lowercase(Locale.US)
    .replace(Regex("[^a-z0-9]+"), "_")
    .trim('_')
