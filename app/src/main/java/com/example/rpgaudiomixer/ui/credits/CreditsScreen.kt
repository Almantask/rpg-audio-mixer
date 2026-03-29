package com.example.rpgaudiomixer.ui.credits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreditsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Behind the Screen", style = MaterialTheme.typography.headlineSmall)
        Text("Arcanum Audio", style = MaterialTheme.typography.titleMedium)
        Text("Version 1.0.0", style = MaterialTheme.typography.bodyMedium)
        Text("Developer: Your Name", style = MaterialTheme.typography.bodyMedium)
        Text("Role: Android Developer", style = MaterialTheme.typography.bodyMedium)
        Text("Links:", style = MaterialTheme.typography.titleSmall)
        Text("- Documentation")
        Text("- Discord community")
        Text("- Contact / support email")
        Text("Made with ❤️ for GMs everywhere", style = MaterialTheme.typography.bodyLarge)
    }
}
