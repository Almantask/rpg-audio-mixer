package com.example.rpgaudiomixer.ui.sessions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.Session
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CampaignSessionsScreen(
    onSessionSelected: (Long) -> Unit = {},
    onBack: () -> Unit = {},
    onGearClick: () -> Unit = {},
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    if (uiState is SessionsUiState.Error) {
        errorMessage = (uiState as SessionsUiState.Error).message
    }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Sessions",
                showBackArrow = true,
                onBack = onBack,
                onGearClick = onGearClick,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is SessionsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SessionsUiState.Success -> {
                    SessionsList(
                        sessions = state.sessions,
                        onSessionSelected = onSessionSelected,
                        onDeleteSession = { viewModel.deleteSession(it) },
                        onAddNew = { showCreateDialog = true },
                    )
                }
                is SessionsUiState.Error -> {
                    EmptyStateView(
                        message = "No sessions yet.",
                        actionLabel = "Add New Session",
                        onAction = { showCreateDialog = true },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateSessionDialog(
            onConfirm = { name ->
                viewModel.createSession(name = name, coverArtUri = null)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }

    ErrorDialog(message = errorMessage, onDismiss = { errorMessage = null })
}

@Composable
private fun SessionsList(
    sessions: List<Session>,
    onSessionSelected: (Long) -> Unit,
    onDeleteSession: (Long) -> Unit,
    onAddNew: () -> Unit,
) {
    if (sessions.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            EmptyStateView(
                message = "No sessions yet for this campaign.",
                actionLabel = "Add New Session",
                onAction = onAddNew,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sessions, key = { it.id }) { session ->
            SwipeToDeleteContainer(
                onDelete = { onDeleteSession(session.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                SessionCard(
                    session = session,
                    onOpen = { onSessionSelected(session.id) },
                )
            }
        }
        item {
            Button(
                onClick = onAddNew,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
            ) {
                Text("+ Add New Session", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: Session,
    onOpen: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpen,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcanumGold,
                )
                if (session.date > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        .format(Date(session.date))
                    Text(text = dateStr, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onOpen,
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
            ) {
                Text("Open →", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun CreateSessionDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Session") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Session name") },
                singleLine = true,
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                enabled = name.isNotBlank(),
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
