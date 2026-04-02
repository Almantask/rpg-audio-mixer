package com.example.rpgaudiomixer.app.screens

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
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Icon
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumCard
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDim
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurface
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceVariant
import com.example.rpgaudiomixer.app.theme.ArcanumPink
import com.example.rpgaudiomixer.app.theme.ArcanumPurple
import com.example.rpgaudiomixer.app.theme.ArcanumSurface
import com.example.rpgaudiomixer.app.viewmodel.AddToSceneViewModel
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

@Composable
fun AddToSceneScreen(
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onCredits: () -> Unit,
    viewModel: AddToSceneViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val isSoundscape = viewModel.mode == "soundscape"
    val title = if (isSoundscape) "ADD SOUNDSCAPE" else "ADD SOUND EFFECT"

    LaunchedEffect(state.isSaving) {
        if (!state.isSaving && state.selectedIds.isEmpty()) {
            // Saving done (selectedIds cleared) — only navigate back if triggered after a confirm
        }
    }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = title,
                onBack = onBack,
                onCredits = onCredits,
            )
        },
        containerColor = ArcanumBlack,
        bottomBar = {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Button(
                    onClick = {
                        viewModel.confirmSelection()
                        onConfirm()
                    },
                    enabled = state.selectedIds.isNotEmpty() && !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ArcanumGold.copy(alpha = 0.15f),
                        contentColor = ArcanumGold,
                        disabledContainerColor = ArcanumGold.copy(alpha = 0.05f),
                        disabledContentColor = ArcanumOnSurfaceVariant,
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.selectedIds.isNotEmpty()) ArcanumGoldDim.copy(alpha = 0.5f) else ArcanumGoldDim.copy(alpha = 0.15f),
                    ),
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = ArcanumGold,
                        )
                    } else {
                        val count = state.selectedIds.size
                        Text(
                            if (count > 0) "CONFIRM ($count)" else "SELECT ITEMS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            if (state.sceneName.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Selecting for: ${state.sceneName}",
                    fontSize = 12.sp,
                    color = ArcanumOnSurfaceVariant,
                    letterSpacing = 0.5.sp,
                )
                Spacer(Modifier.height(8.dp))
            }

            if (state.items.isEmpty()) {
                EmptyAddState(isSoundscape)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
                ) {
                    if (isSoundscape) {
                        @Suppress("UNCHECKED_CAST")
                        val cats = state.items as List<SoundscapeCategory>
                        items(cats, key = { it.id }) { category ->
                            SelectableRow(
                                id = category.id,
                                name = category.name,
                                subtitle = "${category.totalTrackCount} tracks",
                                isSelected = category.id in state.selectedIds,
                                accentColor = ArcanumPurple,
                                iconVector = Icons.Default.MusicNote,
                                onToggle = { viewModel.toggleSelection(category.id) },
                            )
                        }
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        val fxList = state.items as List<FXTrack>
                        items(fxList, key = { it.id }) { fxTrack ->
                            SelectableRow(
                                id = fxTrack.id,
                                name = fxTrack.name,
                                subtitle = if (fxTrack.tags.isNotEmpty()) fxTrack.tags.joinToString(", ") else "No tags",
                                isSelected = fxTrack.id in state.selectedIds,
                                accentColor = ArcanumPink,
                                iconVector = Icons.Default.AudioFile,
                                onToggle = { viewModel.toggleSelection(fxTrack.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectableRow(
    id: Long,
    name: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: androidx.compose.ui.graphics.Color,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.08f) else ArcanumCard)
            .border(
                1.5.dp,
                if (isSelected) accentColor.copy(alpha = 0.5f) else ArcanumGoldDim.copy(alpha = 0.15f),
                RoundedCornerShape(10.dp),
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Check circle
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isSelected) accentColor else ArcanumSurface)
                .border(
                    1.5.dp,
                    if (isSelected) accentColor else ArcanumGoldDim.copy(alpha = 0.3f),
                    CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isSelected) {
                Icon(Icons.Default.Check, null, tint = ArcanumBlack, modifier = Modifier.size(12.dp))
            }
        }
        Spacer(Modifier.width(14.dp))
        Icon(iconVector, null, tint = if (isSelected) accentColor else ArcanumOnSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) accentColor else ArcanumOnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(subtitle, fontSize = 11.sp, color = ArcanumOnSurfaceVariant.copy(alpha = 0.7f), maxLines = 1)
        }
    }
}

@Composable
private fun EmptyAddState(isSoundscape: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            if (isSoundscape) Icons.Default.MusicNote else Icons.Default.AudioFile,
            null,
            tint = ArcanumGoldDim.copy(alpha = 0.3f),
            modifier = Modifier.size(52.dp),
        )
        Spacer(Modifier.height(14.dp))
        Text(
            if (isSoundscape)
                "No soundscape categories found.\nCreate one in the Library first."
            else
                "No sound effects found.\nImport some audio files in the Library.",
            fontSize = 14.sp,
            color = ArcanumOnSurfaceVariant.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 20.sp,
        )
    }
}
