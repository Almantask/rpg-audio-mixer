package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen
import com.example.rpgaudiomixer.ui.sessions.SessionsScreen
import com.example.rpgaudiomixer.ui.sessionscenes.SessionScenesScreen

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
                onNavigateToSessions = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
                onNavigateToCredits = {
                    // TODO: Navigate to credits screen when implemented
                }
            )
        }
        composable(
            route = "campaigns/{campaignId}/sessions",
            arguments = listOf(
                navArgument("campaignId") { type = NavType.StringType }
            )
        ) {
            SessionsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSessionScenes = { sessionId ->
                    navController.navigate("sessions/$sessionId/scenes")
                },
                onNavigateToCredits = {
                    // TODO: Navigate to credits screen when implemented
                }
            )
        }
        composable(
            route = "sessions/{sessionId}/scenes",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) {
            SessionScenesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToActiveScene = { sceneId ->
                    // TODO: Navigate to active scene when implemented
                },
                onNavigateToCredits = {
                    // TODO: Navigate to credits screen when implemented
                }
            )
        }
        composable(MainNavDestination.SCENES.route) {
            ScenesScreen(
                onNavigateToActiveScene = { sceneId ->
                    // TODO: Navigate to active scene when implemented
                },
                onNavigateToCredits = {
                    // TODO: Navigate to credits screen when implemented
                }
            )
        }
        composable(MainNavDestination.LIBRARY.route) {
            LibraryScreen()
        }
    }
}
