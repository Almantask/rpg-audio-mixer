package com.example.rpgaudiomixer.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.HOME.route,
        modifier = modifier
    ) {
        composable(MainNavDestination.HOME.route) {
            PlaceholderScreen(MainNavDestination.HOME.label)
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            PlaceholderScreen(MainNavDestination.CAMPAIGNS.label)
        }
        composable(MainNavDestination.SCENES.route) {
            PlaceholderScreen(MainNavDestination.SCENES.label)
        }
        composable(MainNavDestination.LIBRARY.route) {
            PlaceholderScreen(MainNavDestination.LIBRARY.label)
        }
    }
}

@Composable
private fun PlaceholderScreen(screenName: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = screenName,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
