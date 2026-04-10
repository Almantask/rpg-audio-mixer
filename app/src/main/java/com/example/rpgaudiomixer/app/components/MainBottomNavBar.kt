package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rpgaudiomixer.app.navigation.MainNavDestination

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            selected = current == MainNavDestination.HOME,
            onClick = { onNavigate(MainNavDestination.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("HOME") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.outline,
                unselectedTextColor = MaterialTheme.colorScheme.outline
            )
        )

        NavigationBarItem(
            selected = current == MainNavDestination.CAMPAIGNS,
            onClick = { onNavigate(MainNavDestination.CAMPAIGNS) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Book,
                    contentDescription = "Campaigns"
                )
            },
            label = { Text("CAMPAIGNS") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.outline,
                unselectedTextColor = MaterialTheme.colorScheme.outline
            )
        )

        NavigationBarItem(
            selected = current == MainNavDestination.SCENES,
            onClick = { onNavigate(MainNavDestination.SCENES) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = "Scenes"
                )
            },
            label = { Text("SCENES") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.outline,
                unselectedTextColor = MaterialTheme.colorScheme.outline
            )
        )

        NavigationBarItem(
            selected = current == MainNavDestination.LIBRARY,
            onClick = { onNavigate(MainNavDestination.LIBRARY) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.LibraryMusic,
                    contentDescription = "Library"
                )
            },
            label = { Text("LIBRARY") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.outline,
                unselectedTextColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
