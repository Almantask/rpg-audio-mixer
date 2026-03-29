


package com.example.rpgaudiomixer.app.components

import androidx.compose.runtime.Composable
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Campaign

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = current == MainNavDestination.HOME,
            onClick = { onNavigate(MainNavDestination.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = current == MainNavDestination.CAMPAIGNS,
            onClick = { onNavigate(MainNavDestination.CAMPAIGNS) },
            icon = { Icon(Icons.Filled.Campaign, contentDescription = "Campaigns") },
            label = { Text("Campaigns") }
        )
        NavigationBarItem(
            selected = current == MainNavDestination.SOUNDBOARD,
            onClick = { onNavigate(MainNavDestination.SOUNDBOARD) },
            icon = { Icon(Icons.Filled.List, contentDescription = "Scenes") },
            label = { Text("Scenes") }
        )
        NavigationBarItem(
            selected = current == MainNavDestination.SOUNDSCAPES,
            onClick = { onNavigate(MainNavDestination.SOUNDSCAPES) },
            icon = { Icon(Icons.Filled.LibraryMusic, contentDescription = "Library") },
            label = { Text("Library") }
        )
    }
}
