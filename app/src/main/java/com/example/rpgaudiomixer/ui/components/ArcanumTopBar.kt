package com.example.rpgaudiomixer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun ArcanumTopBar(
    onCredits: () -> Unit,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ArcanumBlack)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ArcanumGold,
                    )
                }
            } else {
                Spacer(modifier = Modifier.padding(start = 48.dp))
            }

            Text(
                text = "✦ ARCANUM AUDIO ✦",
                style = MaterialTheme.typography.titleLarge,
                color = ArcanumGold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = onCredits) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Credits",
                    tint = ArcanumGold,
                )
            }
        }
    }
}
