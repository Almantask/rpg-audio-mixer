package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.ui.activescene.ActiveSceneScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.credits.CreditsScreen
import com.example.rpgaudiomixer.ui.home.HomeScreen
import com.example.rpgaudiomixer.ui.library.LibraryScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen
import com.example.rpgaudiomixer.ui.sessions.CampaignSessionsScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.HOME.name,
        modifier = modifier,
    ) {
        composable(MainNavDestination.HOME.name) {
            HomeScreen(
                onEnterCampaign = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
                onGearClick = { navController.navigate("credits") },
            )
        }
        composable(MainNavDestination.CAMPAIGNS.name) {
            CampaignsScreen(
                onCampaignSelected = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
                onGearClick = { navController.navigate("credits") },
            )
        }
        composable(
            route = "campaigns/{campaignId}/sessions",
            arguments = listOf(navArgument("campaignId") { type = NavType.LongType }),
        ) {
            CampaignSessionsScreen(
                onSessionSelected = { sessionId ->
                    navController.navigate("sessions/$sessionId/scene-select")
                },
                onBack = { navController.navigateUp() },
                onGearClick = { navController.navigate("credits") },
            )
        }
        composable(MainNavDestination.SCENES.name) {
            ScenesScreen(
                onSceneSelected = { sceneId ->
                    navController.navigate("scenes/$sceneId/active")
                },
                onGearClick = { navController.navigate("credits") },
            )
        }
        composable(
            route = "scenes/{sceneId}/active",
            arguments = listOf(navArgument("sceneId") { type = NavType.LongType }),
        ) {
            ActiveSceneScreen(
                sceneName = "Scene",
                onBack = { navController.navigateUp() },
                onGearClick = { navController.navigate("credits") },
            )
        }
        composable(MainNavDestination.LIBRARY.name) {
            LibraryScreen(
                onGearClick = { navController.navigate("credits") },
            )
        }
        composable("credits") {
            CreditsScreen(onBack = { navController.navigateUp() })
        }
    }
}
