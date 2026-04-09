package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceMuted
import com.example.rpgaudiomixer.app.theme.ArcanumSurface

object BottomNavTestTags {
    fun item(destination: MainNavDestination): String = "BottomNav_${destination.label}"
}

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit,
) {
    val rootDestinations = listOf(
        MainNavDestination.HOME,
        MainNavDestination.CAMPAIGNS,
        MainNavDestination.SCENES,
        MainNavDestination.LIBRARY,
    )

    NavigationBar(containerColor = ArcanumSurface) {
        rootDestinations.forEach { destination ->
            NavigationBarItem(
                modifier = Modifier.testTag(BottomNavTestTags.item(destination)),
                selected = destination == current,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = iconForRootDestination(destination),
                        contentDescription = destination.label,
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 10.sp),
                        textAlign = TextAlign.Center,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ArcanumGold,
                    selectedTextColor = ArcanumGold,
                    unselectedIconColor = ArcanumOnSurfaceMuted,
                    unselectedTextColor = ArcanumOnSurfaceMuted,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            )
        }
    }
}

private fun iconForRootDestination(destination: MainNavDestination) = when (destination) {
    MainNavDestination.HOME -> Icons.Default.Home
    MainNavDestination.CAMPAIGNS -> Icons.Default.MenuBook
    MainNavDestination.SCENES -> Icons.Default.Collections
    MainNavDestination.LIBRARY -> Icons.Default.LibraryMusic
    MainNavDestination.SETTINGS,
    MainNavDestination.CAMPAIGN_SESSIONS,
    MainNavDestination.TRASH -> error(
        "Developer error: $destination is not a root-level tab destination and should not appear in bottom navigation.",
    )
}
