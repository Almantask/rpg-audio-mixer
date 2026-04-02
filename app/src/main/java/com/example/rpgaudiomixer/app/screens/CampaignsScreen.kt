package com.example.rpgaudiomixer.app.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumCard
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDim
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurface
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceVariant
import com.example.rpgaudiomixer.app.theme.ArcanumSurface
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.app.viewmodel.CampaignsViewModel
import com.example.rpgaudiomixer.domain.model.Campaign

@Composable
fun CampaignsScreen(
    onEnterCampaign: (Long) -> Unit,
    onCredits: () -> Unit,
    viewModel: CampaignsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { ArcanumTopBar(title = "CAMPAIGNS", onCredits = onCredits) },
        containerColor = ArcanumBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                containerColor = ArcanumGold,
                contentColor = Color(0xFF1A0E00),
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Campaign")
            }
        },
    ) { innerPadding ->
        if (state.campaigns.isEmpty()) {
            EmptyCampaignsState(
                modifier = Modifier.padding(innerPadding),
                onCreate = { viewModel.showCreateDialog() },
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
            ) {
                items(state.campaigns, key = { it.id }) { campaign ->
                    CampaignCard(
                        campaign = campaign,
                        onResume = {
                            viewModel.onCampaignResumed(campaign.id)
                            onEnterCampaign(campaign.id)
                        },
                        onDelete = { viewModel.deleteCampaign(campaign.id) },
                    )
                }
            }
        }
    }

    if (state.showCreateDialog) {
        CreateCampaignDialog(
            onDismiss = { viewModel.dismissCreateDialog() },
            onCreate = { name, uri -> viewModel.createCampaign(name, uri) },
        )
    }
}

@Composable
private fun CampaignCard(
    campaign: Campaign,
    onResume: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCard)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onResume() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Cover art or placeholder
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ArcanumSurfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (campaign.coverArtUri != null) {
                AsyncImage(
                    model = campaign.coverArtUri,
                    contentDescription = campaign.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = ArcanumGoldDim,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = campaign.name,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ArcanumOnSurface,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Button(
                onClick = onResume,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArcanumGold,
                    contentColor = Color(0xFF1A0E00),
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 12.dp, vertical = 4.dp
                ),
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("RESUME", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ArcanumOnSurfaceVariant, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun EmptyCampaignsState(modifier: Modifier, onCreate: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                tint = ArcanumGoldDim,
                modifier = Modifier.size(80.dp),
            )
            Text(
                text = "No tales yet",
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                color = ArcanumOnSurfaceVariant,
            )
            Button(
                onClick = onCreate,
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold, contentColor = Color(0xFF1A0E00)),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("SCRIBE NEW TALE", fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
        }
    }
}

@Composable
private fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var coverArtUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
            coverArtUri = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumSurface,
        titleContentColor = ArcanumGold,
        textContentColor = ArcanumOnSurface,
        title = { Text("New Campaign", fontFamily = FontFamily.Serif) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Campaign Name", color = ArcanumOnSurfaceVariant) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcanumGold,
                        unfocusedBorderColor = ArcanumGoldDim.copy(alpha = 0.4f),
                        focusedTextColor = ArcanumOnSurface,
                        unfocusedTextColor = ArcanumOnSurface,
                    ),
                )
                TextButton(
                    onClick = { imagePicker.launch(arrayOf("image/*")) },
                    colors = ButtonDefaults.textButtonColors(contentColor = ArcanumGold),
                ) {
                    Text(if (coverArtUri != null) "Cover art selected ✓" else "Pick Cover Art")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onCreate(name.trim(), coverArtUri) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold, contentColor = Color(0xFF1A0E00)),
            ) {
                Text("CREATE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = ArcanumOnSurfaceVariant)) {
                Text("CANCEL")
            }
        },
    )
}
