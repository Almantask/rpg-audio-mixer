package com.example.rpgaudiomixer.ui.credits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.ArcanumGold

@Composable
fun CreditsScreen(
    onBack: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Behind the Screen",
                showBackArrow = true,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "🏰",
                style = MaterialTheme.typography.displayMedium,
            )
            Text(
                text = "Arcanum Audio",
                style = MaterialTheme.typography.headlineMedium,
                color = ArcanumGold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A RPG tabletop audio companion for Game Masters.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Version",
                style = MaterialTheme.typography.labelLarge,
                color = ArcanumGold,
            )
            Text(
                text = "1.0.0",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Made with ♥ for adventurers everywhere.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
