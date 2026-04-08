package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.rpgaudiomixer.app.navigation.MainNavDestination

/**
 * Main bottom navigation bar for Arcanum Audio
 * 4 tabs: HOME (🏰), CAMPAIGNS (📖), SCENES (🖼), LIBRARY (🎵)
 */
@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        MainNavDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = current == destination,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(
                        text = destination.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.testTag("BottomNav_${destination.name}")
            )
        }
    }
}

/**
 * Icon mapping for each navigation destination
 * Using Material Icons as approximations for: 🏰 castle, 📖 storybook, 🖼 picture frame, 🎵 music note
 */
private val MainNavDestination.icon: ImageVector
    get() = when (this) {
        MainNavDestination.HOME -> Icons.Default.Home // 🏰 Castle approximation
        MainNavDestination.CAMPAIGNS -> Icons.Default.MenuBook // 📖 Storybook
        MainNavDestination.SCENES -> Icons.Default.PhotoLibrary // 🖼 Picture frame
        MainNavDestination.LIBRARY -> Icons.Default.LibraryMusic // 🎵 Music note
    }
