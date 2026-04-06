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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import com.example.rpgaudiomixer.app.theme.ArcanumBackground
import com.example.rpgaudiomixer.app.theme.ArcanumPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcanumTopBar(
    title: String,
    showBackArrow: Boolean = false,
    onBack: (() -> Unit)? = null,
    onGearClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                color = ArcanumPrimary,
                modifier = Modifier.testTag("ArcanumTopBar_Title"),
            )
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(
                    onClick = { onBack?.invoke() },
                    modifier = Modifier.testTag("ArcanumTopBar_BackArrow"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ArcanumPrimary,
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onGearClick,
                modifier = Modifier.testTag("ArcanumTopBar_GearIcon"),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = ArcanumPrimary,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ArcanumBackground,
            titleContentColor = ArcanumPrimary,
            navigationIconContentColor = ArcanumPrimary,
            actionIconContentColor = ArcanumPrimary,
        ),
    )
}
