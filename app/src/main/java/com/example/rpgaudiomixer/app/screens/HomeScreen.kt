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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SpatialAudioOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumCard
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDim
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurface
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceVariant
import com.example.rpgaudiomixer.app.theme.ArcanumPurple
import com.example.rpgaudiomixer.app.theme.ArcanumSurface
import com.example.rpgaudiomixer.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onEnterCampaign: (Long) -> Unit,
    onEnterScene: (Long) -> Unit,
    onEnterScenePlay: (Long) -> Unit,
    onCredits: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "ARCANUM AUDIO",
                onCredits = onCredits,
            )
        },
        containerColor = ArcanumBlack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // App title
            Text(
                text = "Welcome, Dungeon Master",
                style = MaterialTheme.typography.headlineLarge,
                color = ArcanumGold,
            )

            // Active Campaign card
            val campaign = state.activeCampaign
            if (campaign != null) {
                ArcanumCard(
                    title = "ACTIVE CAMPAIGN",
                    subtitle = campaign.name,
                    icon = Icons.Default.Campaign,
                    actionLabel = "ENTER DOMAIN",
                    onAction = { onEnterCampaign(campaign.id) },
                )
            } else {
                EmptyHeroCard(
                    label = "No active campaign",
                    hint = "Create a campaign in the Campaigns tab",
                )
            }

            // Resume Journey card
            val lastScene = state.lastScene
            if (lastScene != null) {
                ArcanumCard(
                    title = "RESUME JOURNEY",
                    subtitle = lastScene.name,
                    icon = Icons.Default.PlayArrow,
                    actionLabel = "ENTER",
                    onAction = { onEnterScenePlay(lastScene.id) },
                )
            }

            // Top Atmosphere
            val topTrack = state.topAtmosphereTrack
            StatCard(
                title = "TOP ATMOSPHERE",
                value = topTrack?.name ?: "—",
                icon = Icons.Default.MusicNote,
            )

            // Legendary Action
            val legendaryFX = state.legendaryFXTrack
            StatCard(
                title = "LEGENDARY ACTION",
                value = legendaryFX?.name ?: "—",
                icon = Icons.Default.SpatialAudioOff,
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ArcanumCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCard)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(20.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = ArcanumGold, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArcanumGoldDim,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = ArcanumOnSurface,
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArcanumGold,
                    contentColor = Color(0xFF1A0E00),
                ),
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ArcanumSurface)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ArcanumPurple.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = ArcanumPurple)
        }
        Column {
            Text(
                text = title,
                fontSize = 9.sp,
                letterSpacing = 1.5.sp,
                color = ArcanumOnSurfaceVariant,
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ArcanumOnSurface,
            )
        }
    }
}

@Composable
private fun EmptyHeroCard(label: String, hint: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCard)
            .border(1.dp, ArcanumGoldDim.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = ArcanumOnSurfaceVariant, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = hint, color = ArcanumOnSurfaceVariant.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}
