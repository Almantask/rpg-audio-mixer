package com.example.rpgaudiomixer.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            PlaceholderScreen(text = "Home")
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            PlaceholderScreen(text = "Campaigns")
        }
        composable(MainNavDestination.SCENES.route) {
            PlaceholderScreen(text = "Scenes")
        }
        composable(MainNavDestination.LIBRARY.route) {
            PlaceholderScreen(text = "Library")
        }
        composable("credits") {
            PlaceholderScreen(text = "Credits")
        }
    }
}

@Composable
private fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}
