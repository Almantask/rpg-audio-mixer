package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
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

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        MainNavDestination.entries.forEach { destination ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = getIconForDestination(destination),
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) },
                selected = current == destination,
                onClick = { onNavigate(destination) },
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

private fun getIconForDestination(destination: MainNavDestination): ImageVector {
    return when (destination) {
        MainNavDestination.HOME -> Icons.Default.Home
        MainNavDestination.CAMPAIGNS -> Icons.Default.Book
        MainNavDestination.SCENES -> Icons.Default.Movie
        MainNavDestination.LIBRARY -> Icons.Default.LibraryMusic
    }
}
