package com.example.rpgaudiomixer.ui.credits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.BuildConfig
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.app.theme.ArcanumGrayMid
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.PrimaryButton

@Composable
fun CreditsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(
            onCredits = {},
            showBack = true,
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Title
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "✦ Behind the Screen ✦",
                    style = MaterialTheme.typography.displaySmall,
                    color = ArcanumGold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "The artisans who forged this tome",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcanumGrayLight,
                    textAlign = TextAlign.Center,
                )
            }

            // Fuel the Forge card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ArcanumCardSurface)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Fuel the Forge",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ArcanumGold,
                )
                Text(
                    text = "Arcanum Audio is a labour of love, built by adventurers for adventurers. " +
                            "If it enhances your table, consider supporting the devs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcanumGrayLight,
                    textAlign = TextAlign.Center,
                )
                PrimaryButton(
                    text = "BUY THE DEVS A POTION",
                    onClick = { /* open donation URL */ },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Dev credits
            SectionCard(title = "Crafted by") {
                CreditsRow(role = "Lead Artificer", name = "Arcanum Team")
                CreditsRow(role = "Sound Design", name = "Open Source Community")
                CreditsRow(role = "UI / UX Enchantment", name = "Arcanum Team")
            }

            // Libraries
            SectionCard(title = "Powered by") {
                CreditsRow(role = "Android Jetpack", name = "Google")
                CreditsRow(role = "Kotlin", name = "JetBrains")
                CreditsRow(role = "Compose", name = "Google")
                CreditsRow(role = "ExoPlayer / Media3", name = "Google")
                CreditsRow(role = "Coil", name = "Coil Contributors")
                CreditsRow(role = "Hilt", name = "Google / Dagger Team")
                CreditsRow(role = "Room", name = "Google")
            }

            // Version
            SectionCard(title = "Version") {
                Text(
                    text = BuildConfig.VERSION_NAME,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcanumGrayLight,
                )
            }

            // Connect
            SectionCard(title = "Connect with the Guild") {
                PrimaryButton(
                    text = "DOCUMENTATION",
                    onClick = { /* open docs URL */ },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                PrimaryButton(
                    text = "JOIN DISCORD",
                    onClick = { /* open Discord URL */ },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                PrimaryButton(
                    text = "SEND A RAVEN (EMAIL)",
                    onClick = { /* open email intent */ },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCardSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = ArcanumGold,
        )
        content()
    }
}

@Composable
private fun CreditsRow(role: String, name: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = role, style = MaterialTheme.typography.bodyMedium, color = ArcanumGrayMid)
        Text(text = name, style = MaterialTheme.typography.bodyMedium, color = Color.White)
    }
}
