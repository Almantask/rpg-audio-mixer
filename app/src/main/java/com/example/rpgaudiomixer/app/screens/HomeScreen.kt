package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.ui.home.HomeViewModel
import com.example.rpgaudiomixer.domain.campaign.Campaign
import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.library.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.Scene

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onCampaignClick: (Long) -> Unit,
    onResumeClick: (Long, Boolean) -> Unit,
    onNavigateToCampaigns: () -> Unit,
    onNavigateToLibrary: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = ArcanumBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // Bottom nav space
        ) {
            HomeHeader()

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ArcanumGold)
                }
            } else {
                ActiveCampaignHero(
                    campaign = uiState.activeCampaign,
                    onClick = { uiState.activeCampaign?.id?.let { onCampaignClick(it) } },
                    onSeeAllActive = onNavigateToCampaigns
                )

                Spacer(modifier = Modifier.height(24.dp))

                ResumeJourneySection(
                    scene = uiState.resumeScene,
                    onClick = { uiState.resumeScene?.id?.let { onResumeClick(it, false) } }
                )

                Spacer(modifier = Modifier.height(24.dp))

                DashboardStatsGrid(
                    topAtmosphere = uiState.topAtmosphere,
                    legendaryAction = uiState.legendaryAction,
                    onAtmosphereClick = onNavigateToLibrary,
                    onActionClick = onNavigateToLibrary
                )
            }
        }
    }
}

@Composable
private fun HomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "BEYOND THE VEIL",
            style = MaterialTheme.typography.labelMedium,
            color = ArcanumMutedGold,
            letterSpacing = 2.sp
        )
        Text(
            text = "Game Master Dashboard",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = ArcanumGold
        )
    }
}

@Composable
private fun ActiveCampaignHero(
    campaign: Campaign?,
    onClick: () -> Unit,
    onSeeAllActive: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACTIVE QUEST",
                style = MaterialTheme.typography.titleSmall,
                color = ArcanumOnSurface.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
            TextButton(onClick = onSeeAllActive) {
                Text("SEE ALL", color = ArcanumMutedGold, fontSize = 12.sp)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() }
        ) {
            if (campaign?.coverArtUri != null) {
                AsyncImage(
                    model = campaign.coverArtUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(ArcanumCard, ArcanumBlack)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Castle,
                        contentDescription = null,
                        tint = ArcanumMutedGold.copy(alpha = 0.3f),
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            // Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, ArcanumBlack.copy(alpha = 0.8f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = campaign?.name ?: "No Active Campaign",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ArcanumGold,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (campaign != null) "Ready to resume your narrative" else "Start a new adventure in the campaigns tab",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcanumOnSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ResumeJourneySection(
    scene: Scene?,
    onClick: () -> Unit
) {
    if (scene == null) return

    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "RESUME JOURNEY",
            style = MaterialTheme.typography.titleSmall,
            color = ArcanumOnSurface.copy(alpha = 0.6f),
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            onClick = onClick,
            colors = CardDefaults.cardColors(containerColor = ArcanumCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(ArcanumGold, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ArcanumOnGold)
                }

                Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                    Text(text = scene.name, style = MaterialTheme.typography.titleMedium, color = ArcanumGold)
                    Text(
                        text = "LAST OPENED SCENE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ArcanumOnSurface.copy(alpha = 0.5f)
                    )
                }

                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ArcanumMutedGold)
            }
        }
    }
}

@Composable
private fun DashboardStatsGrid(
    topAtmosphere: SoundscapeTrack?,
    legendaryAction: FxTrack?,
    onAtmosphereClick: () -> Unit,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "TOP ATMOSPHERE",
            title = topAtmosphere?.name ?: "Silence",
            icon = Icons.Default.MusicNote,
            onClick = onAtmosphereClick
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "LEGENDARY ACTION",
            title = legendaryAction?.name ?: "None",
            icon = Icons.Default.FlashOn,
            onClick = onActionClick
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    label: String,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = ArcanumCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = ArcanumMutedGold, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = ArcanumOnSurface.copy(alpha = 0.4f))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = ArcanumGold,
                maxLines = 1,
                fontSize = 14.sp
            )
        }
    }
}
