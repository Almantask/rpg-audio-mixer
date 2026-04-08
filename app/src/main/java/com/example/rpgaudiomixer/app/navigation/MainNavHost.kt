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
import com.example.rpgaudiomixer.ui.library.LibraryScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen
import com.example.rpgaudiomixer.ui.sessions.CampaignSessionsScreen
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
            com.example.rpgaudiomixer.ui.home.HomeScreen(
                onNavigateToCampaign = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
                onNavigateToScene = { sceneId ->
                    navController.navigate("scenes/$sceneId/active")
                }
            )
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
            LibraryScreen(
                onNavigateToComposer = { categoryId ->
                    navController.navigate("library/soundscapes/$categoryId/compose")
                }
            )
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
            route = "sessions/{sessionId}/scenes",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: return@composable
            SessionScenesScreen(
                sessionId = sessionId,
                sessionName = "Session", // TODO: Pass actual session name
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScene = { sceneId ->
                    navController.navigate("scenes/$sceneId/active")
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
        composable(
            route = "library/soundscapes/{categoryId}/compose",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: return@composable
            com.example.rpgaudiomixer.ui.library.soundscapes.SoundscapeComposerScreen(
                categoryId = categoryId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("credits") {
            com.example.rpgaudiomixer.ui.credits.CreditsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTrash = { navController.navigate("credits/trash") }
            )
        }
        composable("credits/trash") {
            com.example.rpgaudiomixer.ui.trash.TrashScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
