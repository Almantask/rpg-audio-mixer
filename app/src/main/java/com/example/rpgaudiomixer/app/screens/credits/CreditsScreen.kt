package com.example.rpgaudiomixer.app.screens.credits

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.BlackBg
import com.example.rpgaudiomixer.app.theme.Gold
import com.example.rpgaudiomixer.app.theme.Typography

@Composable
fun CreditsScreen(
    onBack: () -> Unit,
    onNavigateToTrash: () -> Unit
) {
    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Behind the Screen",
                showBackArrow = true,
                onBack = onBack
            )
        },
        containerColor = BlackBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Identity
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(Color.DarkGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                // Using a placeholder icon for now
                Icon(
                    imageVector = Icons.Default.Sync, 
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Gold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Arcanum Audio",
                style = Typography.displaySmall,
                color = Gold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Version 1.0.0",
                style = Typography.bodyMedium,
                color = Gold.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Actions
            Button(
                onClick = { /* TODO: Sync purchases */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold.copy(alpha = 0.1f),
                    contentColor = Gold,
                    disabledContainerColor = Gold.copy(alpha = 0.05f),
                    disabledContentColor = Gold.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = false // Grayed out as per spec
            ) {
                Icon(Icons.Default.Sync, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SYNC PURCHASES & FREE TRACKS")
            }
            Text(
                text = "(Available once per day)",
                style = Typography.labelSmall,
                color = Gold.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onNavigateToTrash,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = BlackBg
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RESTORE RECENT DELETES")
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Credits Section
            CreditHeader(title = "CREDITS")
            CreditItem(name = "Arcanum Developer", role = "Design & Development")
            CreditItem(name = "Arcanum Artists", role = "Audio & Visual Assets")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Links Section
            CreditHeader(title = "LINKS")
            LinkItem(icon = Icons.AutoMirrored.Filled.MenuBook, label = "Documentation", onClick = {})
            LinkItem(icon = Icons.Default.Chat, label = "Discord community", onClick = {})
            LinkItem(icon = Icons.Default.Email, label = "Contact / support email", onClick = {})
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Made with ❤️ for GMs everywhere",
                style = Typography.bodyMedium,
                color = Gold.copy(alpha = 0.6f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CreditHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = Typography.labelLarge.copy(letterSpacing = 2.sp),
            color = Gold,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        HorizontalDivider(modifier = Modifier.weight(1f), color = Gold.copy(alpha = 0.3f))
    }
}

@Composable
private fun CreditItem(name: String, role: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = name, style = Typography.bodyLarge, color = Color.White)
        Text(text = role, style = Typography.bodySmall, color = Color.Gray)
    }
}

@Composable
private fun LinkItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, style = Typography.bodyLarge, color = Color.White)
        }
    }
}
