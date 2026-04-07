package com.example.rpgaudiomixer.ui.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.Session

/**
 * Campaign Sessions screen showing all sessions for a specific campaign.
 *
 * Features:
 * - Hero banner with campaign info
 * - Scrollable list of session cards
 * - Empty state with "Add New Session" prompt
 * - FAB to create new session
 * - Swipe-to-delete sessions
 */
@Composable
fun CampaignSessionsScreen(
    onNavigateToSessionScenes: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampaignSessionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Sessions",
                showBackArrow = true,
                onBackClick = onNavigateBack,
                onGearClick = onNavigateToSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.testTag("Sessions_CreateFab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Session")
            }
        },
        modifier = modifier
    ) { padding ->
        when (val state = uiState) {
            is SessionsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is SessionsUiState.Success -> {
                if (state.sessions.isEmpty()) {
                    EmptySessionsState(
                        onCreateClick = { showCreateDialog = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                } else {
                    SessionsList(
                        sessions = state.sessions,
                        onSessionClick = onNavigateToSessionScenes,
                        onDeleteSession = viewModel::deleteSession,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                }
            }
            is SessionsUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = viewModel::clearError
                )
            }
        }

        if (showCreateDialog) {
            CreateSessionDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, date, coverUri ->
                    viewModel.createSession(name, date, coverUri)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
private fun SessionsList(
    sessions: List<Session>,
    onSessionClick: (Long) -> Unit,
    onDeleteSession: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.testTag("Sessions_List"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = sessions,
            key = { it.id }
        ) { session ->
            SessionCard(
                session = session,
                onClick = { onSessionClick(session.id) },
                onDelete = { onDeleteSession(session.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EmptySessionsState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.testTag("Sessions_EmptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📖",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No sessions yet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start your first adventure",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateClick,
            modifier = Modifier.testTag("Sessions_EmptyState_CreateButton")
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add New Session")
        }
    }
}

@Composable
private fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, date: Long, coverUri: String?) -> Unit
) {
    var sessionName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Session") },
        text = {
            Column {
                OutlinedTextField(
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    label = { Text("Session Name") },
                    placeholder = { Text("e.g. Session 1, Chapter 5") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CreateSession_NameField")
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Date will be set to today",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (sessionName.isNotBlank()) {
                        onCreate(
                            sessionName.trim(),
                            System.currentTimeMillis(),
                            null
                        )
                    }
                },
                enabled = sessionName.isNotBlank(),
                modifier = Modifier.testTag("CreateSession_ConfirmButton")
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("CreateSession_CancelButton")
            ) {
                Text("Cancel")
            }
        }
    )
}
