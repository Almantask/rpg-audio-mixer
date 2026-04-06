package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumCard
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceDim

private data class NavItem(
    val destination: MainNavDestination,
    val icon: ImageVector,
    val label: String,
)

private val navItems = listOf(
    NavItem(MainNavDestination.HOME, Icons.Filled.Home, "Home"),
    NavItem(MainNavDestination.CAMPAIGNS, Icons.Filled.MenuBook, "Campaigns"),
    NavItem(MainNavDestination.SCENES, Icons.Filled.Photo, "Scenes"),
    NavItem(MainNavDestination.LIBRARY, Icons.Filled.LibraryMusic, "Library"),
)

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit,
) {
    NavigationBar(
        containerColor = ArcanumCard,
        contentColor = ArcanumGold,
    ) {
        navItems.forEach { item ->
            val selected = item.destination == current
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.destination) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ArcanumGold,
                    selectedTextColor = ArcanumGold,
                    unselectedIconColor = ArcanumOnSurfaceDim,
                    unselectedTextColor = ArcanumOnSurfaceDim,
                    indicatorColor = ArcanumBlack,
                ),
            )
        }
    }
}
