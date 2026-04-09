package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.rpgaudiomixer.app.theme.ArcanumGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcanumTopBar(
    title: String,
    showBackArrow: Boolean = false,
    onBack: () -> Unit = {},
    onGearClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = ArcanumGold,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = ArcanumGold
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onGearClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Credits",
                    tint = ArcanumGold
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            titleContentColor = ArcanumGold,
            navigationIconContentColor = ArcanumGold,
            actionIconContentColor = ArcanumGold
        )
    )
}
