package com.example.rpgaudiomixer.app.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.CampaignCard
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSessions: (Long) -> Unit,
    onNavigateToActiveScene: (Long, Boolean) -> Unit,
    onNavigateToCredits: () -> Unit,
    onNavigateToCampaigns: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "ARCANUM AUDIO",
                onGearClick = onNavigateToCredits
            )
        },
        containerColor = BlackBg
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
            // Active Campaign Section
            item {
                SectionHeader("ACTIVE CAMPAIGN")
                if (uiState.activeCampaign != null) {
                    CampaignCard(
                        campaign = uiState.activeCampaign!!,
                        onResume = { onNavigateToSessions(it) }
                    )
                } else {
                    EmptyHomeCard(
                        icon = Icons.Default.AutoStories,
                        message = "NO ACTIVE LEGEND",
                        ctaText = "BEGIN TALE",
                        onCtaClick = onNavigateToCampaigns
                    )
                }
            }

            // Resume Journey Section
            if (uiState.activeCampaign != null) {
                item {
                    SectionHeader("RESUME JOURNEY")
                    if (uiState.resumeScene != null) {
                        SceneCard(
                            scene = uiState.resumeScene!!,
                            onPlay = { onNavigateToActiveScene(uiState.resumeScene!!.id, true) },
                            onClick = { onNavigateToActiveScene(uiState.resumeScene!!.id, false) }
                        )
                    } else {
                        EmptyHomeCard(
                            icon = Icons.Default.History,
                            message = "NO RECENT PATHS",
                            ctaText = "OPEN COMPASS",
                            onCtaClick = { onNavigateToSessions(uiState.activeCampaign!!.id) }
                        )
                    }
                }
            }

            // Stats Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "TOP ATMOSPHERE",
                        trackName = uiState.topAtmosphere?.name ?: "SILENCE",
                        categoryName = uiState.topAtmosphereCategory ?: "NONE",
                        icon = Icons.Default.MusicNote
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "LEGENDARY ACTION",
                        trackName = uiState.legendaryAction?.name ?: "STILLNESS",
                        categoryName = uiState.legendaryActionCategory ?: "NONE",
                        icon = Icons.Default.Warning
                    )
                }
            }
        }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = Typography.labelLarge,
        color = Gold.copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 8.dp),
        letterSpacing = 2.sp
    )
}

@Composable
fun EmptyHomeCard(
    icon: ImageVector,
    message: String,
    ctaText: String,
    onCtaClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = Shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gold.copy(alpha = 0.3f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = Typography.bodyLarge,
                color = Gold.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCtaClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = BlackBg
                ),
                shape = Shapes.small
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(ctaText, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("→")
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    trackName: String,
    categoryName: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = Shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = Typography.labelSmall.copy(fontSize = 10.sp),
                color = Gold.copy(alpha = 0.5f),
                letterSpacing = 1.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trackName,
                style = Typography.bodyMedium,
                color = Gold,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "• $categoryName",
                style = Typography.bodySmall.copy(fontSize = 11.sp),
                color = Gold.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}
