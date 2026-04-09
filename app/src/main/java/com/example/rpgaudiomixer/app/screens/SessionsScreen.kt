package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.SessionCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.ui.sessions.CampaignSessionsViewModel
import com.example.rpgaudiomixer.app.ui.sessions.SessionsUiState
import com.example.rpgaudiomixer.domain.session.Session

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    campaignName: String,
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
    onSessionClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(campaignName, color = ArcanumGold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ArcanumGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArcanumBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = ArcanumGold,
                contentColor = ArcanumOnGold
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Session")
            }
        },
        containerColor = ArcanumBlack
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is SessionsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = ArcanumGold
                    )
                }
                is SessionsUiState.Success -> {
                    if (state.sessions.isEmpty()) {
                        EmptyStateView(
                            illustration = Icons.Default.AutoStories,
                            title = "No sessions recorded",
                            subtitle = "Every campaign needs a beginning.",
                            actionLabel = "SCRIBE FIRST SESSION",
                            onAction = { showCreateDialog = true }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.sessions, key = { it.id }) { session ->
                                SwipeToDeleteContainer(
                                    onDelete = { viewModel.deleteSession(session.id) }
                                ) {
                                    SessionCard(
                                        session = session,
                                        onClick = { onSessionClick(session.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                is SessionsUiState.Error -> {
                    Text(
                        text = state.message,
                        color = ArcanumErrorRed,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            if (showCreateDialog) {
                CreateSessionDialog(
                    onDismiss = { showCreateDialog = false },
                    onConfirm = { name ->
                        viewModel.createSession(name)
                        showCreateDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumCard,
        title = { Text("New Session", color = ArcanumGold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Session Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ArcanumGold,
                    focusedLabelColor = ArcanumGold,
                    unfocusedLabelColor = ArcanumOnSurface.copy(alpha = 0.7f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("SCRIBE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = ArcanumOnSurface)
            }
        }
    )
}
