package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumMutedText
import com.example.rpgaudiomixer.app.theme.ArcanumSurface

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit
) {
    NavigationBar(
        containerColor = ArcanumSurface,
    ) {
        MainNavDestination.mainDestinations.forEach { destination ->
            NavigationBarItem(
                modifier = Modifier.testTag(destination.testTag),
                selected = destination == current,
                onClick = { onNavigate(destination) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ArcanumGold,
                    selectedTextColor = ArcanumGold,
                    indicatorColor = ArcanumGold.copy(alpha = 0.16f),
                    unselectedIconColor = ArcanumMutedText,
                    unselectedTextColor = ArcanumMutedText,
                ),
                icon = {
                    Icon(
                        imageVector = when (destination) {
                            MainNavDestination.HOME -> Icons.Outlined.Home
                            MainNavDestination.CAMPAIGNS -> Icons.Outlined.AutoStories
                            MainNavDestination.SCENES -> Icons.Outlined.CollectionsBookmark
                            MainNavDestination.LIBRARY -> Icons.Outlined.LibraryMusic
                        },
                        contentDescription = destination.label,
                    )
                },
                label = {
                    Text(destination.label)
                },
            )
        }
    }
}
