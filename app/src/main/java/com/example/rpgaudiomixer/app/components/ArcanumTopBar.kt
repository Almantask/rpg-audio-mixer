package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcanumTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackArrow: Boolean = false,
    onBack: (() -> Unit)? = null,
    onGearClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.testTag("ArcanumTopBar_Title")
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (showBackArrow && onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("ArcanumTopBar_BackArrow")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            actions()
            if (onGearClick != null) {
                IconButton(
                    onClick = onGearClick,
                    modifier = Modifier.testTag("ArcanumTopBar_GearIcon")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}
