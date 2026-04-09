package com.example.rpgaudiomixer.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.screens.MainScreenTestTags

@Composable
fun HomeRoute(
    onOpenCampaign: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val activeCampaign by viewModel.activeCampaign.collectAsState(initial = null)
    HomeScreen(
        activeCampaignName = activeCampaign?.name,
        onOpenCampaign = {
            activeCampaign?.id?.let(onOpenCampaign)
        },
    )
}

@Composable
fun HomeScreen(
    activeCampaignName: String?,
    onOpenCampaign: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag(MainScreenTestTags.HOME),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Home screen")
        if (activeCampaignName == null) {
            Text("No active campaign yet")
        } else {
            Card(
                colors = CardDefaults.cardColors(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("Active campaign")
                    Text(activeCampaignName)
                    Button(onClick = onOpenCampaign) {
                        Text("Enter Domain")
                    }
                }
            }
        }
    }
}
