package com.example.rpgaudiomixer.ui.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.domain.model.Session
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.EmptyState
import com.example.rpgaudiomixer.ui.components.PrimaryButton

@Composable
fun SessionsScreen(
    campaignId: Long,
    onOpenSession: (Long) -> Unit,
    onBack: () -> Unit,
    onCredits: () -> Unit,
    viewModel: SessionsViewModel = hiltViewModel(),
) {
    viewModel.init(campaignId)
    val state by viewModel.uiState(campaignId).collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(
            onCredits = onCredits,
            showBack = true,
            onBack = onBack,
        )

        // Campaign title as section header
        state.campaign?.let { campaign ->
            Text(
                text = campaign.name,
                style = MaterialTheme.typography.displayMedium,
                color = ArcanumGold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            if (state.sessions.isEmpty() && !state.isLoading) {
                item {
                    EmptyState(
                        title = "No Sessions Yet",
                        subtitle = "Begin a new chapter for this campaign.",
                    )
                }
            }

            items(state.sessions, key = { it.id }) { session ->
                SessionCard(
                    session = session,
                    onOpen = { onOpenSession(session.id) },
                )
            }

            item {
                PrimaryButton(
                    text = "+ ADD NEW SESSION",
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showAddDialog) {
        AddSessionDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, description ->
                viewModel.addSession(name, description)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun SessionCard(session: Session, onOpen: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .height(160.dp)
            .background(ArcanumCardSurface)
            .clickable { onOpen() },
    ) {
        if (session.coverArtUri != null) {
            AsyncImage(
                model = session.coverArtUri,
                contentDescription = session.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Text(
                text = session.name,
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (session.description.isNotBlank()) {
                Text(
                    text = session.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ArcanumGrayLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun AddSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Session Name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), description.trim()) },
            ) {
                Text("BEGIN", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        },
    )
}
