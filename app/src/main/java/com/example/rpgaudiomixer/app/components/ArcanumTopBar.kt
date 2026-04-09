package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.rpgaudiomixer.app.theme.ArcanumGold

object ArcanumTopBarTestTags {
    const val TITLE = "ArcanumTopBar_Title"
    const val GEAR_ICON = "ArcanumTopBar_GearIcon"
    const val BACK_ARROW = "ArcanumTopBar_BackArrow"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcanumTopBar(
    title: String,
    showBackArrow: Boolean,
    onBack: () -> Unit,
    onGearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier.testTag(ArcanumTopBarTestTags.TITLE),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = ArcanumGold,
            )
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(
                    modifier = Modifier.testTag(ArcanumTopBarTestTags.BACK_ARROW),
                    onClick = onBack,
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        // Preserve the Material icon tint supplied by the IconButton context.
                        tint = Color.Unspecified,
                    )
                }
            }
        },
        actions = {
            IconButton(
                modifier = Modifier.testTag(ArcanumTopBarTestTags.GEAR_ICON),
                onClick = onGearClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    // Preserve the Material icon tint supplied by the IconButton context.
                    tint = Color.Unspecified,
                )
            }
        },
    )
}
