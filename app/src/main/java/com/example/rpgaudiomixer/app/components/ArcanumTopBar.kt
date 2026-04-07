package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow

/**
 * Arcanum Audio Top App Bar
 *
 * Reusable top bar with:
 * - Title (gold typography)
 * - Optional back arrow
 * - Settings gear icon (always present)
 *
 * @param title Screen title
 * @param showBackArrow Show/hide back arrow
 * @param onBackClick Callback for back arrow click
 * @param onSettingsClick Callback for settings gear click (navigates to Credits)
 * @param actions Additional actions to display (optional)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcanumTopBar(
    title: String,
    showBackArrow: Boolean = false,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.testTag("ArcanumTopBar_Title")
            )
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("ArcanumTopBar_BackArrow")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigate back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            actions()
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("ArcanumTopBar_GearIcon")
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
