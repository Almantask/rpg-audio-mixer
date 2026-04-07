package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rpgaudiomixer.app.navigation.MainNavDestination

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        MainNavDestination.entries.forEach { destination ->
            val selected = current == destination
            NavigationBarItem(
                modifier = Modifier.testTag("BottomNav_${destination.label}"),
                selected = selected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = getIconForDestination(destination),
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label.uppercase()) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                )
            )
        }
    }
}

private fun getIconForDestination(destination: MainNavDestination): ImageVector {
    return when (destination) {
        MainNavDestination.HOME -> Icons.Default.Home // 🏰 Castle placeholder
        MainNavDestination.CAMPAIGNS -> Icons.Default.MenuBook // 📖 Storybook placeholder
        MainNavDestination.SCENES -> Icons.Default.Photo // 🖼 Picture frame placeholder
        MainNavDestination.LIBRARY -> Icons.Default.LibraryMusic // 🎵 Music note placeholder
    }
}
