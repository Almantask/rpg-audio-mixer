package com.example.rpgaudiomixer.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.rpgaudiomixer.app.theme.ArcanumBorder
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDark
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.app.theme.ArcanumGrayMid
import com.example.rpgaudiomixer.app.theme.ArcanumSliderPurple
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.FxEffect
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.EmptyState
import com.example.rpgaudiomixer.ui.components.PrimaryButton

@Composable
fun HomeScreen(
    onEnterCampaign: (Long) -> Unit,
    onEnterScene: (Long) -> Unit,
    onCredits: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(onCredits = onCredits)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Active Campaign Hero
            Text(
                text = "Active Campaign",
                style = MaterialTheme.typography.titleMedium,
                color = ArcanumGrayLight,
            )
            if (state.activeCampaign != null) {
                CampaignHeroCard(
                    campaign = state.activeCampaign!!,
                    onEnter = { onEnterCampaign(state.activeCampaign!!.id) },
                )
            } else {
                EmptyState(
                    title = "No Active Campaign",
                    subtitle = "Head to Campaigns to start a new tale.",
                )
            }

            // Resume Journey
            if (state.lastScene != null) {
                Text(
                    text = "Resume Journey",
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcanumGrayLight,
                )
                ResumeJourneyCard(
                    scene = state.lastScene!!,
                    onEnter = { onEnterScene(state.lastScene!!.id) },
                )
            }

            // Top Atmosphere
            state.topAtmosphereScene?.let { scene ->
                Text(
                    text = "Top Atmosphere",
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcanumGrayLight,
                )
                TrackStatCard(
                    name = scene.name,
                    subtitle = "AMBIENCE · LOOPABLE",
                    badge = "${scene.playCount} PLAYS",
                    badgeColor = ArcanumGoldDark,
                )
            }

            // Legendary Action
            state.legendaryFx?.let { fx ->
                Text(
                    text = "Legendary Action",
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcanumGrayLight,
                )
                TrackStatCard(
                    name = fx.name,
                    subtitle = "FX · SUDDEN",
                    badge = "${fx.playCount} CASTS",
                    badgeColor = ArcanumSliderPurple,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CampaignHeroCard(campaign: Campaign, onEnter: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .height(220.dp)
            .background(ArcanumCardSurface),
    ) {
        if (campaign.coverArtUri != null) {
            AsyncImage(
                model = campaign.coverArtUri,
                contentDescription = campaign.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)))
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Text(
                text = campaign.name,
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (campaign.description.isNotBlank()) {
                Text(
                    text = campaign.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ArcanumGrayLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(text = "ENTER DOMAIN", onClick = onEnter)
        }
    }
}

@Composable
private fun ResumeJourneyCard(scene: Scene, onEnter: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .height(160.dp)
            .background(ArcanumCardSurface),
    ) {
        if (scene.coverArtUri != null) {
            AsyncImage(
                model = scene.coverArtUri,
                contentDescription = scene.name,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(ArcanumGold, CircleShape)
                )
                Text(
                    text = "  LAST ACTIVE LOCATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcanumGold,
                )
            }
            Text(
                text = scene.name,
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
            )
            if (scene.description.isNotBlank()) {
                Text(
                    text = scene.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ArcanumGrayLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        PrimaryButton(
            text = "ENTER",
            onClick = onEnter,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .fillMaxWidth(0.4f),
        )
    }
}

@Composable
private fun TrackStatCard(
    name: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCardSurface)
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = "Play $name",
                        tint = ArcanumGold,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = ArcanumGrayMid,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(ArcanumBorder),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(2.dp)
                        .background(badgeColor),
                )
            }
        }
    }
}
