package com.example.rpgaudiomixer.app.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumTextMuted

@Composable
fun MainBottomNavBar(
    current: MainNavDestination,
    onNavigate: (MainNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = androidx.compose.ui.unit.dp(8f)
    ) {
        MainNavDestination.entries.forEach { destination ->
            val selected = current == destination
            NavigationBarItem(
                modifier = Modifier.testTag("BottomNav_${destination.label}"),
                selected = selected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = getIconForDestination(destination),
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(
                        text = destination.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ArcanumGold,
                    selectedTextColor = ArcanumGold,
                    unselectedIconColor = ArcanumTextMuted,
                    unselectedTextColor = ArcanumTextMuted,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

private fun getIconForDestination(destination: MainNavDestination): ImageVector {
    return when (destination) {
        MainNavDestination.HOME -> Icons.Default.Home
        MainNavDestination.CAMPAIGNS -> Icons.Default.Book
        MainNavDestination.SCENES -> Icons.Default.PhotoLibrary
        MainNavDestination.LIBRARY -> Icons.Default.MusicNote
    }
}
