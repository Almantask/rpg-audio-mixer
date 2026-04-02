package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumCard
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDim
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurface
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceVariant
import com.example.rpgaudiomixer.app.theme.ArcanumPurple
import com.example.rpgaudiomixer.app.theme.ArcanumSurface

@Composable
fun CreditsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "BEHIND THE SCREEN",
                onBack = onBack,
                onCredits = null,
            )
        },
        containerColor = ArcanumBlack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // App title block
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.MenuBook,
                    null,
                    tint = ArcanumGold,
                    modifier = Modifier.size(48.dp),
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "ARCANUM AUDIO",
                    fontFamily = FontFamily.Serif,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArcanumGold,
                    letterSpacing = 2.sp,
                )
                Text(
                    "v1.0.0",
                    fontSize = 12.sp,
                    color = ArcanumOnSurfaceVariant,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Set the mood. Rule the table.",
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    color = ArcanumOnSurfaceVariant.copy(alpha = 0.7f),
                )
            }

            Spacer(Modifier.height(32.dp))
            SectionDivider()
            Spacer(Modifier.height(24.dp))

            // About
            CreditsSection(title = "THE LORE") {
                CreditsBody(
                    "Arcanum Audio is an ambient sound mixer crafted for tabletop RPG game masters. " +
                        "Build campaigns and sessions, compose layered soundscapes by intensity, " +
                        "and trigger one-shot sound effects for every dramatic moment at your table."
                )
            }

            Spacer(Modifier.height(24.dp))
            SectionDivider()
            Spacer(Modifier.height(24.dp))

            // Tech credits
            CreditsSection(title = "MADE WITH") {
                CreditsRow("Kotlin & Jetpack Compose", "UI Layer")
                CreditsRow("ExoPlayer (media3)", "Audio Engine")
                CreditsRow("Room Database", "Persistence")
                CreditsRow("Hilt", "Dependency Injection")
            }

            Spacer(Modifier.height(24.dp))
            SectionDivider()
            Spacer(Modifier.height(24.dp))

            // Contact
            CreditsSection(title = "CONTACT THE ARCHMAGE") {
                CreditsLinkRow(
                    icon = Icons.Default.Email,
                    text = "arcanum.audio@example.com",
                )
                CreditsLinkRow(
                    icon = Icons.Default.Star,
                    text = "Rate on Google Play",
                )
            }

            Spacer(Modifier.height(32.dp))

            // Footer
            Text(
                "© 2025 Arcanum Audio. All rights reserved.\nFor the adventurers and the storytellers.",
                fontSize = 11.sp,
                color = ArcanumOnSurfaceVariant.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 16.sp,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CreditsSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = ArcanumGoldDim,
        )
        content()
    }
}

@Composable
private fun CreditsBody(text: String) {
    Text(
        text,
        fontSize = 14.sp,
        color = ArcanumOnSurface.copy(alpha = 0.85f),
        lineHeight = 22.sp,
    )
}

@Composable
private fun CreditsRow(label: String, value: String? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = ArcanumOnSurface, fontWeight = FontWeight.Medium)
        if (value != null) Text(value, fontSize = 12.sp, color = ArcanumOnSurfaceVariant)
    }
}

@Composable
private fun CreditsLinkRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = ArcanumPurple, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 13.sp, color = ArcanumOnSurface)
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(color = ArcanumGoldDim.copy(alpha = 0.15f), thickness = 1.dp)
}
