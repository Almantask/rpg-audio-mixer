package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumMutedText
import com.example.rpgaudiomixer.app.theme.ArcanumSurface
import com.example.rpgaudiomixer.app.navigation.MainNavDestination

object MainBottomNavBarTestTags {
    fun item(destination: MainNavDestination) = "BottomNav_${destination.label}"
}

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit
) {
    NavigationBar(
        containerColor = ArcanumSurface,
    ) {
        MainNavDestination.mainTabs.forEach { destination ->
            NavigationBarItem(
                modifier = androidx.compose.ui.Modifier.testTag(
                    MainBottomNavBarTestTags.item(destination),
                ),
                selected = destination == current,
                onClick = { onNavigate(destination) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ArcanumGold,
                    selectedTextColor = ArcanumGold,
                    indicatorColor = ArcanumGold.copy(alpha = 0.16f),
                    unselectedIconColor = ArcanumMutedText,
                    unselectedTextColor = ArcanumMutedText,
                ),
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                    )
                },
                label = {
                    Text(text = destination.label)
                },
            )
        }
    }
}

private val MainNavDestination.icon: ImageVector
    get() = when (this) {
        MainNavDestination.HOME -> Icons.Default.Cast
        MainNavDestination.CAMPAIGNS -> Icons.Default.AutoStories
        MainNavDestination.SCENES -> Icons.Default.Collections
        MainNavDestination.LIBRARY -> Icons.Default.LibraryMusic
    }
