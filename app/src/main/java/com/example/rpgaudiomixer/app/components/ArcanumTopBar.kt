package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumSurface

object ArcanumTopBarTags {
    const val TITLE = "ArcanumTopBar_Title"
    const val GEAR_ICON = "ArcanumTopBar_GearIcon"
    const val BACK_ARROW = "ArcanumTopBar_BackArrow"
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ArcanumTopBar(
    title: String,
    showBackArrow: Boolean,
    onBack: () -> Unit,
    onGearClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ArcanumSurface,
            titleContentColor = ArcanumGold,
            navigationIconContentColor = ArcanumGold,
            actionIconContentColor = ArcanumGold,
        ),
        title = {
            Text(
                modifier = Modifier.testTag(ArcanumTopBarTags.TITLE),
                text = title,
            )
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(
                    modifier = Modifier.testTag(ArcanumTopBarTags.BACK_ARROW),
                    onClick = onBack,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        },
        actions = {
            IconButton(
                modifier = Modifier.testTag(ArcanumTopBarTags.GEAR_ICON),
                onClick = onGearClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                )
            }
        },
    )
}
