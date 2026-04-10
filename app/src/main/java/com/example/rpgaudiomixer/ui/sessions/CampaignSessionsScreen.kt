package com.example.rpgaudiomixer.ui.sessions

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.LoadingStateView
import com.example.rpgaudiomixer.app.components.SessionCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.Session

@Composable
fun CampaignSessionsScreen(
    onOpenSession: (Session) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (val state = uiState) {
            CampaignSessionsUiState.Loading -> LoadingStateView(label = "Loading sessions…")
            is CampaignSessionsUiState.Error -> ErrorDialog(message = state.message, onDismiss = { })
            is CampaignSessionsUiState.Success -> {
                CampaignHeroBanner(
                    title = state.campaign?.name ?: "Campaign Sessions",
                    coverArtUri = state.campaign?.coverArtUri,
                )
                if (state.sessions.isEmpty()) {
                    EmptyStateView(
                        title = "No sessions yet",
                        actionLabel = "Add New Session",
                        onAction = { showCreateDialog = true },
                        illustration = "📜",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.sessions, key = { session -> session.id }) { session ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.deleteSession(session.id) },
                            ) {
                                SessionCard(
                                    session = session,
                                    onOpen = onOpenSession,
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showCreateDialog = true },
        ) {
            Text("Add New Session")
        }
    }

    if (showCreateDialog) {
        CreateSessionDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, dateMillis, coverArtUri ->
                viewModel.createSession(name = name, dateMillis = dateMillis, coverArtUri = coverArtUri)
                showCreateDialog = false
            },
        )
    }
}

@Composable
private fun CampaignHeroBanner(
    title: String,
    coverArtUri: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.BottomStart,
    ) {
        AsyncImage(
            model = coverArtUri,
            contentDescription = "$title hero banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
private fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Long, String?) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var coverArtUri by rememberSaveable { mutableStateOf<String?>(null) }
    var dateMillis by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        coverArtUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Session name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                ) {
                    Text("Cover Art")
                }
                Text("Selected date: ${java.text.DateFormat.getDateInstance().format(java.util.Date(dateMillis))}")
                Text(
                    text = coverArtUri?.let { "Selected cover art: $it" } ?: "No cover art selected",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, dateMillis, coverArtUri) },
                enabled = name.trim().isNotBlank(),
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
}
