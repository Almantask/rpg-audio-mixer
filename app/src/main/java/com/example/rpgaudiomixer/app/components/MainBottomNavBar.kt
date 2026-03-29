package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.rpgaudiomixer.app.navigation.MainNavDestination

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit
) {
    data class DestinationItem(
        val destination: MainNavDestination,
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val label: String
    )

    val destinations = listOf(
        DestinationItem(MainNavDestination.HOME, Icons.Default.Home, "Home"),
        DestinationItem(MainNavDestination.CAMPAIGNS, Icons.Default.Book, "Campaigns"),
        DestinationItem(MainNavDestination.SCENES, Icons.Default.PhotoLibrary, "Scenes"),
        DestinationItem(MainNavDestination.LIBRARY, Icons.Default.LibraryMusic, "Library")
    )

    NavigationBar {
        destinations.forEach { item ->
            NavigationBarItem(
                selected = current == item.destination,
                onClick = { onNavigate(item.destination) },
                icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
