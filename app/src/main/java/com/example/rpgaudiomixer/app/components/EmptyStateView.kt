package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.BlackBg
import com.example.rpgaudiomixer.app.theme.Gold
import com.example.rpgaudiomixer.app.theme.Shapes
import com.example.rpgaudiomixer.app.theme.Typography

@Composable
fun EmptyStateView(
    illustration: ImageVector = Icons.Default.AutoStories,
    message: String,
    ctaText: String,
    onCtaClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = illustration,
                contentDescription = null,
                tint = Gold.copy(alpha = 0.3f),
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = message,
                style = Typography.headlineLarge,
                color = Gold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onCtaClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = BlackBg
                ),
                shape = Shapes.medium
            ) {
                Text(
                    text = ctaText.uppercase(),
                    style = Typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}
