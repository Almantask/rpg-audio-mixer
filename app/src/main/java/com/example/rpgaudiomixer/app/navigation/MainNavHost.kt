package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.app.screens.*
import com.example.rpgaudiomixer.ui.activescene.ActiveSceneSoundscapesScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen

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
            HomeScreen()
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            CampaignsScreen(
                onNavigateToCampaign = { campaignId ->
                    // TODO: Navigate to campaign sessions
                }
            )
        }
        composable(MainNavDestination.SCENES.route) {
            ScenesScreen()
        }
        composable(MainNavDestination.LIBRARY.route) {
            LibraryScreen()
        }
        composable(
            route = "scenes/{sceneId}/active",
            arguments = listOf(
                navArgument("sceneId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val sceneId = backStackEntry.arguments?.getLong("sceneId") ?: return@composable
            ActiveSceneSoundscapesScreen(
                sceneId = sceneId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
