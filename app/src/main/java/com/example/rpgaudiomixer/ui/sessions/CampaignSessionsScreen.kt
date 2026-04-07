package com.example.rpgaudiomixer.ui.sessions

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.ui.common.UiState

@Composable
fun CampaignSessionsScreen(
    onSessionClick: (Long) -> Unit,
    viewModel: CampaignSessionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyStateView(
                        message = "No sessions yet. Start your campaign!",
                        buttonText = "Add New Session",
                        onButtonClick = { showCreateDialog = true }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data, key = { it.id }) { session ->
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
            is UiState.Error -> {
                EmptyStateView(
                    message = state.message,
                    buttonText = "Retry",
                    onButtonClick = { }
                )
            }
        }

        if (uiState is UiState.Success) {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .padding(16.dp)
                    .align(androidx.compose.ui.Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Session")
            }
        }
    }

    if (showCreateDialog) {
        CreateSessionDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, uri ->
                viewModel.createSession(name, uri)
                showCreateDialog = false
            }
        )
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = { viewModel.clearError() }
    )
}

@Composable
private fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        coverUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Session Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text(if (coverUri != null) "Change Cover Art" else "Add Cover Art")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, coverUri?.toString()) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
