package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.app.screens.*
import com.example.rpgaudiomixer.ui.activescene.ActiveSceneScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen
import com.example.rpgaudiomixer.ui.sessions.CampaignSessionsScreen

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
                    navController.navigate("campaigns/$campaignId/sessions")
                }
            )
        }
        composable(MainNavDestination.SCENES.route) {
            ScenesScreen(
                onNavigateToScene = { sceneId ->
                    navController.navigate("scenes/$sceneId/active")
                }
            )
        }
        composable(MainNavDestination.LIBRARY.route) {
            LibraryScreen()
        }
        composable(
            route = "campaigns/{campaignId}/sessions",
            arguments = listOf(
                navArgument("campaignId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getLong("campaignId") ?: return@composable
            CampaignSessionsScreen(
                campaignId = campaignId,
                campaignName = "Campaign", // TODO: Pass actual campaign name
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSession = { sessionId ->
                    navController.navigate("sessions/$sessionId/scenes")
                }
            )
        }
        composable(
            route = "scenes/{sceneId}/active",
            arguments = listOf(
                navArgument("sceneId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val sceneId = backStackEntry.arguments?.getLong("sceneId") ?: return@composable
            ActiveSceneScreen(
                sceneId = sceneId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
