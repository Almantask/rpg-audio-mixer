package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceDim

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcanumTopBar(
    title: String,
    showBackArrow: Boolean = false,
    onBack: () -> Unit = {},
    onGearClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            )
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ArcanumGold,
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onGearClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = ArcanumOnSurfaceDim,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ArcanumBlack,
            titleContentColor = ArcanumGold,
        ),
    )
}
