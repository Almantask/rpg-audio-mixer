package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.theme.ArcanumBackground
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceVariant
import com.example.rpgaudiomixer.app.theme.ArcanumPrimary

private data class NavItem(
    val destination: MainNavDestination,
    val icon: ImageVector,
    val testTag: String,
)

private val navItems = listOf(
    NavItem(MainNavDestination.HOME, Icons.Default.Home, "BottomNav_Home"),
    NavItem(MainNavDestination.CAMPAIGNS, Icons.Default.Star, "BottomNav_Campaigns"),
    NavItem(MainNavDestination.SCENES, Icons.Default.Photo, "BottomNav_Scenes"),
    NavItem(MainNavDestination.LIBRARY, Icons.Default.MusicNote, "BottomNav_Library"),
)

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit,
) {
    NavigationBar(
        containerColor = ArcanumBackground,
    ) {
        navItems.forEach { item ->
            val selected = item.destination == current
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.destination) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.destination.label,
                    )
                },
                label = { Text(text = item.destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ArcanumPrimary,
                    selectedTextColor = ArcanumPrimary,
                    unselectedIconColor = ArcanumOnSurfaceVariant,
                    unselectedTextColor = ArcanumOnSurfaceVariant,
                    indicatorColor = ArcanumBackground,
                ),
                modifier = Modifier.testTag(item.testTag),
            )
        }
    }
}
