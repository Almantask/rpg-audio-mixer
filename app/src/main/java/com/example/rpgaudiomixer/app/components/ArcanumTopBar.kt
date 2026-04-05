package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.rpgaudiomixer.app.theme.BlackBg
import com.example.rpgaudiomixer.app.theme.Gold
import com.example.rpgaudiomixer.app.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcanumTopBar(
    title: String,
    showBackArrow: Boolean = false,
    onBack: () -> Unit = {},
    onGearClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {
        IconButton(onClick = onGearClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Gold
            )
        }
    }
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = Typography.titleLarge,
                color = Gold
            )
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Gold
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = BlackBg,
            titleContentColor = Gold,
            navigationIconContentColor = Gold,
            actionIconContentColor = Gold
        )
    )
}
