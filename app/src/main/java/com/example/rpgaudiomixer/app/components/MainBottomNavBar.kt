package com.example.rpgaudiomixer.app.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumMutedGold
import com.example.rpgaudiomixer.app.theme.ArcanumSurface

@Composable
fun MainBottomNavBar(
    current: MainNavDestination?,
    modifier: Modifier = Modifier,
    onNavigate: (MainNavDestination) -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = ArcanumSurface,
    ) {
        MainNavDestination.entries.forEach { destination ->
            NavigationBarItem(
                modifier = Modifier.testTag("BottomNav_${destination.label}"),
                selected = current == destination,
                onClick = { onNavigate(destination) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ArcanumGold,
                    selectedTextColor = ArcanumGold,
                    unselectedIconColor = ArcanumMutedGold,
                    unselectedTextColor = ArcanumMutedGold,
                    indicatorColor = ArcanumSurface,
                ),
                icon = {
                    androidx.compose.material3.Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title,
                    )
                },
                label = {
                    Text(destination.label)
                },
            )
        }
    }
}
