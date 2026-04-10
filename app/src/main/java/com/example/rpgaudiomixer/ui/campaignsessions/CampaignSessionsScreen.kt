package com.example.rpgaudiomixer.ui.campaignsessions

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SessionCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.components.toDisplayDate
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.ui.UiState

@Composable
fun CampaignSessionsRoute(
    onOpenSession: (Long) -> Unit,
    onTitleChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
) {
    val campaign by viewModel.campaign.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    LaunchedEffect(campaign?.name) {
        onTitleChange(campaign?.name)
    }
    DisposableEffect(Unit) {
        onDispose { onTitleChange(null) }
    }

    CampaignSessionsScreen(
        campaign = campaign,
        uiState = uiState,
        errorMessage = errorMessage,
        onOpenSession = onOpenSession,
        onCreateSession = viewModel::createSession,
        onDeleteSession = viewModel::deleteSession,
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@Composable
private fun CampaignSessionsScreen(
    campaign: Campaign?,
    uiState: UiState<List<Session>>,
    errorMessage: String?,
    onOpenSession: (Long) -> Unit,
    onCreateSession: (String, Long?, String?) -> Unit,
    onDeleteSession: (Long) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.AutoStories,
                    title = "Unable to load sessions",
                    body = uiState.message,
                    actionLabel = "Dismiss",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                CampaignSessionsContent(
                    campaign = campaign,
                    sessions = uiState.data,
                    onOpenSession = onOpenSession,
                    onCreateClick = { showCreateDialog = true },
                    onDeleteSession = onDeleteSession,
                )
            }
        }

        if (showCreateDialog) {
            CreateSessionDialog(
                onDismiss = { showCreateDialog = false },
                onCreateSession = { name, dateMillis, coverArtUri ->
                    onCreateSession(name, dateMillis, coverArtUri)
                    if (name.isNotBlank() && dateMillis != null) {
                        showCreateDialog = false
                    }
                },
            )
        }
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = onDismissError,
    )
}

@Composable
private fun CampaignSessionsContent(
    campaign: Campaign?,
    sessions: List<Session>,
    onOpenSession: (Long) -> Unit,
    onCreateClick: () -> Unit,
    onDeleteSession: (Long) -> Unit,
) {
    if (sessions.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            campaign?.let { CampaignHeroBanner(campaign = it) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                EmptyStateView(
                    icon = Icons.Rounded.AutoStories,
                    title = "No sessions yet",
                    body = "Add a new session to start linking scenes to this campaign.",
                    actionLabel = "Add New Session",
                    onAction = onCreateClick,
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            if (campaign != null) {
                CampaignHeroBanner(campaign = campaign)
            }
        }
        items(items = sessions, key = { session -> session.id }) { session ->
            SwipeToDeleteContainer(
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
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreateClick,
            ) {
                Text("+ ADD NEW SESSION")
            }
        }
    }
}

@Composable
private fun CampaignHeroBanner(
    campaign: Campaign,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(MaterialTheme.shapes.large)
            .background(ArcanumSurfaceVariant)
            .padding(20.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Image,
                contentDescription = null,
                tint = ArcanumGold,
            )
            Text(
                text = campaign.name,
                style = MaterialTheme.typography.headlineSmall,
                color = ArcanumGold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = campaign.coverArtUri ?: "No cover art selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreateSession: (String, Long?, String?) -> Unit,
) {
    var sessionName by rememberSaveable { mutableStateOf("") }
    var selectedCoverUri by rememberSaveable { mutableStateOf<String?>(null) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var selectedDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        selectedCoverUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    label = { Text("Session name") },
                    singleLine = true,
                )
                OutlinedButton(onClick = { showDatePicker = true }) {
                    Text(
                        if (selectedDateMillis == null) {
                            "Choose session date"
                        } else {
                            selectedDateMillis!!.toDisplayDate()
                        },
                    )
                }
                OutlinedButton(
                    onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = null,
                    )
                    Text("Choose cover art")
                }
                Text(
                    text = selectedCoverUri ?: "No cover art selected",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateSession(sessionName, selectedDateMillis, selectedCoverUri)
                },
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    },
                ) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
