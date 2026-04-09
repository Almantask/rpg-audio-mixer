package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.ui.activescene.ActiveSceneSoundscapesScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.credits.CreditsScreen
import com.example.rpgaudiomixer.ui.library.LibraryScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen
import com.example.rpgaudiomixer.ui.sessions.SessionsScreen
import com.example.rpgaudiomixer.ui.sessionscenes.SessionScenesScreen
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeCategoryComposerScreen
import com.example.rpgaudiomixer.ui.trash.TrashScreen

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
                onNavigateToCampaign = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
                onNavigateToActiveScene = { sceneId, autoplay ->
                    navController.navigate("scenes/$sceneId/active?autoplay=$autoplay")
                }
            )
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            CampaignsScreen(
                onNavigateToSessions = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
                onNavigateToCredits = {
                    navController.navigate("credits")
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
                    navController.navigate("credits")
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
                onNavigateToActiveScene = { sceneId, autoplay, campaignId ->
                    navController.navigate("scenes/$sceneId/active?autoplay=$autoplay&campaignId=$campaignId")
                },
                onNavigateToCredits = {
                    navController.navigate("credits")
                }
            )
        }
        composable(MainNavDestination.SCENES.route) {
            ScenesScreen(
                onNavigateToActiveScene = { sceneId, autoplay ->
                    navController.navigate("scenes/$sceneId/active?autoplay=$autoplay")
                },
                onNavigateToCredits = {
                    navController.navigate("credits")
                }
            )
        }
        composable(MainNavDestination.LIBRARY.route) {
            LibraryScreen(
                onNavigateToComposer = { categoryId ->
                    navController.navigate("library/soundscapes/$categoryId/compose")
                },
                onNavigateToCredits = {
                    navController.navigate("credits")
                }
            )
        }
        composable(
            route = "library/soundscapes/{categoryId}/compose",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType }
            )
        ) {
            SoundscapeCategoryComposerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCredits = {
                    navController.navigate("credits")
                }
            )
        }
        composable(
            route = "scenes/{sceneId}/active?autoplay={autoplay}&campaignId={campaignId}",
            arguments = listOf(
                navArgument("sceneId") { type = NavType.StringType },
                navArgument("autoplay") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("campaignId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            ActiveSceneSoundscapesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCredits = {
                    navController.navigate("credits")
                }
            )
        }
        composable("credits") {
            CreditsScreen(
                onNavigateToTrash = {
                    navController.navigate("credits/trash")
                }
            )
        }
        composable("credits/trash") {
            TrashScreen()
        }
    }
}
