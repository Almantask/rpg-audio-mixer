package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumGold

@Composable
fun HomeScreen() {
    MainScreenPlaceholder(
        title = "Welcome Home",
        body = "The Home dashboard will surface the active campaign, resume journey, and featured audio stats.",
    )
}

@Composable
fun CampaignsScreen() {
    MainScreenPlaceholder(
        title = "Campaigns",
        body = "Your campaign library will live here once iteration 1 lands.",
    )
}

@Composable
fun CreditsScreen() {
    MainScreenPlaceholder(
        title = "Behind the Screen",
        body = "Credits, version details, and support links will be added in a later iteration.",
    )
}

@Composable
private fun MainScreenPlaceholder(
    title: String,
    body: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArcanumBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = ArcanumGold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        }
    }
}
