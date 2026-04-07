package com.example.rpgaudiomixer.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * Main navigation host for Arcanum Audio
 *
 * Routes: HOME, CAMPAIGNS, SCENES, LIBRARY
 * Each tab has a placeholder composable for now.
 */
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
            PlaceholderScreen("Home")
        }

        composable(MainNavDestination.CAMPAIGNS.route) {
            PlaceholderScreen("Campaigns")
        }

        composable(MainNavDestination.SCENES.route) {
            PlaceholderScreen("Scenes")
        }

        composable(MainNavDestination.LIBRARY.route) {
            PlaceholderScreen("Library")
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$name Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
