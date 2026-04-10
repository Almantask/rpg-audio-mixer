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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    onBack: () -> Unit,
    onNavigateToTrash: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CREDITS", color = ArcanumGold, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ArcanumGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArcanumBlack)
            )
        },
        containerColor = ArcanumBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(ArcanumGold, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Castle, contentDescription = null, tint = ArcanumOnGold, modifier = Modifier.size(60.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ARCANUM AUDIO",
                style = MaterialTheme.typography.headlineLarge,
                color = ArcanumGold,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = ArcanumMutedGold
            )

            Spacer(modifier = Modifier.height(32.dp))

            CreditsSection(
                title = "DEVELOPMENT",
                items = listOf("Lead Scribe: AI Antigravity", "Grand Architect: Google Deepmind")
            )

            Spacer(modifier = Modifier.height(24.dp))

            CreditsSection(
                title = "LEGAL",
                items = listOf("Terms of Enchantment", "Privacy Scrolls")
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNavigateToTrash,
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = ArcanumGold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("RESTORE RECENT DELETES", color = ArcanumGold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /* Placeholder for sync */ },
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SYNC PURCHASES", color = ArcanumMutedGold)
            }
            
            Text(
                text = "Cloud sync coming in future expansion packs",
                style = MaterialTheme.typography.labelSmall,
                color = ArcanumOnSurface.copy(alpha = 0.4f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun CreditsSection(title: String, items: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = ArcanumOnSurface.copy(alpha = 0.5f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { item ->
            Text(
                text = item,
                style = MaterialTheme.typography.bodyLarge,
                color = ArcanumOnSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
