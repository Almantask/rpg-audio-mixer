package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fort
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.theme.BlackBg
import com.example.rpgaudiomixer.app.theme.Gold

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit
) {
    NavigationBar(
        containerColor = BlackBg,
        contentColor = Gold
    ) {
        val items = listOf(
            NavigationItemData(MainNavDestination.HOME, Icons.Default.Fort, "HOME"),
            NavigationItemData(MainNavDestination.CAMPAIGNS, Icons.Default.MenuBook, "CAMPAIGNS"),
            NavigationItemData(MainNavDestination.SCENES, Icons.Default.Image, "SCENES"),
            NavigationItemData(MainNavDestination.LIBRARY, Icons.Default.LibraryMusic, "LIBRARY")
        )

        items.forEach { item ->
            NavigationBarItem(
                selected = current == item.destination,
                onClick = { onNavigate(item.destination) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Gold,
                    selectedTextColor = Gold,
                    unselectedIconColor = Gold.copy(alpha = 0.5f),
                    unselectedTextColor = Gold.copy(alpha = 0.5f),
                    indicatorColor = Gold.copy(alpha = 0.1f)
                )
            )
        }
    }
}

private data class NavigationItemData(
    val destination: MainNavDestination,
    val icon: ImageVector,
    val label: String
)
