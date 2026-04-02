package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDim
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceVariant
import com.example.rpgaudiomixer.app.theme.ArcanumSurface

private data class NavItem(
    val destination: MainNavDestination,
    val label: String,
    val icon: ImageVector,
)

private val navItems = listOf(
    NavItem(MainNavDestination.HOME, "HOME", Icons.Default.Home),
    NavItem(MainNavDestination.CAMPAIGNS, "CAMPAIGNS", Icons.Default.AutoStories),
    NavItem(MainNavDestination.SCENES, "SCENES", Icons.Default.Layers),
    NavItem(MainNavDestination.LIBRARY, "LIBRARY", Icons.Default.LibraryMusic),
)

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit,
) {
    NavigationBar(
        containerColor = ArcanumSurface,
        contentColor = ArcanumGold,
        tonalElevation = androidx.compose.ui.unit.Dp(0f),
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
                label = {
                    Text(
                        text = item.label,
                        fontSize = 9.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        letterSpacing = 1.sp,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ArcanumGold,
                    selectedTextColor = ArcanumGold,
                    indicatorColor = ArcanumGold.copy(alpha = 0.15f),
                    unselectedIconColor = ArcanumOnSurfaceVariant,
                    unselectedTextColor = ArcanumOnSurfaceVariant,
                ),
            )
        }
    }
}

