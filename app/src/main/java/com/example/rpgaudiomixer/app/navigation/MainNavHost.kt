package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.app.screens.campaigns.CampaignsScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.CAMPAIGNS.name,
        modifier = modifier
    ) {
        composable(MainNavDestination.HOME.name) {
            // TODO: HomeScreen
        }
        composable(MainNavDestination.CAMPAIGNS.name) {
            CampaignsScreen(
                onNavigateToSessions = { campaignId ->
                    // navController.navigate("campaigns/$campaignId/sessions")
                }
            )
        }
        composable(MainNavDestination.SCENES.name) {
            // TODO: ScenesScreen
        }
        composable(MainNavDestination.LIBRARY.name) {
            // TODO: LibraryScreen
        }
    }
}
