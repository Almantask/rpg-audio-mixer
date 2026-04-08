package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow

/**
 * Reusable top app bar for Arcanum Audio
 * - Gold title typography
 * - ⚙️ gear icon always present → navigates to Credits
 * - Optional back arrow
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcanumTopBar(
    title: String,
    onGearClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackArrow: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    TopAppBar(
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
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onGearClick,
                modifier = Modifier.testTag("ArcanumTopBar_GearIcon")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Credits",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    )
}
