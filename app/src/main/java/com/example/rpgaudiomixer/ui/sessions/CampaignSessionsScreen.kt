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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.common.UiState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CampaignSessionsScreen(
    campaignId: Long,
    campaignName: String,
    onNavigateBack: () -> Unit,
    onNavigateToSession: (Long) -> Unit,
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    LaunchedEffect(campaignId) {
        viewModel.loadSessions(campaignId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = campaignName,
                showBackArrow = true,
                onBack = onNavigateBack,
                onGearClick = { /* Navigate to settings */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.semantics { contentDescription = "AddSession" }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Session")
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = onNavigateBack
                )
            }

            is UiState.Success -> {
                CampaignSessionsContent(
                    sessions = state.data,
                    onSessionClick = onNavigateToSession,
                    onDeleteSession = viewModel::deleteSession,
                    modifier = modifier.padding(paddingValues)
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateSessionDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, date ->
                viewModel.createSession(name, date)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun CampaignSessionsContent(
    sessions: List<com.example.rpgaudiomixer.domain.model.Session>,
    onSessionClick: (Long) -> Unit,
    onDeleteSession: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (sessions.isEmpty()) {
        EmptyStateView(
            title = "No Sessions",
            message = "Create your first session to start your campaign journey",
            actionLabel = "CREATE SESSION",
            onActionClick = { /* Trigger create dialog */ }
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                items = sessions,
                key = { it.id }
            ) { session ->
                SwipeToDeleteContainer(
                    onDelete = { onDeleteSession(session.id) }
                ) {
                    SessionCard(
                        name = session.name,
                        date = session.date,
                        onClick = { onSessionClick(session.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var date by remember {
        // Default to today's date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(dateFormat.format(Date()))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Session") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Session Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "SessionNameInput" }
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "SessionDateInput" }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && date.isNotBlank()) {
                        onCreate(name.trim(), date.trim())
                    }
                },
                enabled = name.isNotBlank() && date.isNotBlank()
            ) {
                Text("CREATE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
