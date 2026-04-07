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
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.sessions.CampaignSessionsScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen
import com.example.rpgaudiomixer.ui.home.HomeScreen
import com.example.rpgaudiomixer.ui.credits.CreditsScreen

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
            HomeScreen(
                onNavigateToCampaigns = {
                    navController.navigate(MainNavDestination.CAMPAIGNS.route)
                }
            )
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            CampaignsScreen(
                onCampaignClick = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                }
            )
        }
        composable(MainNavDestination.SCENES.route) {
            ScenesScreen(
                onSceneClick = { sceneId ->
                    navController.navigate("scenes/$sceneId/active")
                }
            )
        }
        composable(MainNavDestination.LIBRARY.route) {
            PlaceholderScreen(text = "Library")
        }
        composable("credits") {
            CreditsScreen()
        }
        composable(
            route = "campaigns/{campaignId}/sessions",
            arguments = listOf(navArgument("campaignId") { type = NavType.StringType })
        ) {
            CampaignSessionsScreen(
                onSessionClick = { sessionId ->
                    navController.navigate("sessions/$sessionId/scenes")
                }
            )
        }
        composable(
            route = "sessions/{sessionId}/scenes",
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            PlaceholderScreen(text = "Session Scenes")
        }
        composable(
            route = "scenes/{sceneId}/active",
            arguments = listOf(navArgument("sceneId") { type = NavType.StringType })
        ) {
            PlaceholderScreen(text = "Active Scene")
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
