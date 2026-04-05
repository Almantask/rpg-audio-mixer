package com.example.rpgaudiomixer.app.screens.campaigns

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.SessionCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun CampaignSessionsScreen(
    campaignId: Long,
    viewModel: CampaignSessionsViewModel = hiltViewModel(),
    onNavigateToScenes: (Long) -> Unit
) {
    LaunchedEffect(campaignId) {
        viewModel.setCampaignId(campaignId)
    }

    val sessions by viewModel.sessions.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BlackBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Gold,
                contentColor = BlackBg,
                shape = Shapes.medium
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Session")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (sessions.isEmpty()) {
                EmptyStateView(
                    illustration = Icons.Default.HistoryEdu,
                    message = "NO TALES YET WRITTEN",
                    ctaText = "ADD NEW SESSION",
                    onCtaClick = { showCreateDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sessions, key = { it.id }) { session ->
                        SwipeToDeleteContainer(
                            onDelete = { viewModel.deleteSession(session.id) }
                        ) {
                            SessionCard(
                                session = session,
                                onClick = { onNavigateToScenes(it) }
                            )
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            SessionCreateDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, coverUri ->
                    viewModel.createSession(name, coverUri)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionCreateDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> coverUri = uri }
    )

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(Shapes.large)
            .background(CardSurface)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "NEW PLAY NIGHT",
                style = Typography.headlineMedium,
                color = Gold,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Cover Picker
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(Shapes.medium)
                    .background(BlackBg),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Pick Cover Art",
                        tint = Gold.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                }
                if (coverUri != null) {
                    Text(text = "SET", color = Gold, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("SESSION NAME", color = Gold.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Gold,
                    unfocusedTextColor = Gold,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Gold,
                    unfocusedIndicatorColor = Gold.copy(alpha = 0.3f),
                    cursorColor = Gold
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Gold
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCEL")
                }
                
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onCreate(name, coverUri?.toString())
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = BlackBg
                    ),
                    enabled = name.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    shape = Shapes.medium
                ) {
                    Text("CREATE", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
