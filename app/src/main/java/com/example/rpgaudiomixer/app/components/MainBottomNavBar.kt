package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Movie
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
 * Arcanum Audio Main Bottom Navigation Bar
 *
 * 4 tabs: HOME, CAMPAIGNS, SCENES, LIBRARY
 * Gold selected icon, muted unselected
 * Persists across all main screens
 *
 * @param currentDestination Currently selected destination
 * @param onNavigate Callback when user taps a nav item
 */
@Composable
fun MainBottomNavBar(
    currentDestination: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        MainNavDestination.entries.forEach { destination ->
            val icon = when (destination) {
                MainNavDestination.HOME -> Icons.Filled.Home
                MainNavDestination.CAMPAIGNS -> Icons.Filled.Campaign
                MainNavDestination.SCENES -> Icons.Filled.Movie
                MainNavDestination.LIBRARY -> Icons.Filled.LibraryMusic
            }

            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("BottomNav_${destination.name}")
            )
        }
    }
}
