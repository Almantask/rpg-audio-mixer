package com.example.rpgaudiomixer.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.app.screens.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.app.screens.credits.CreditsScreen
import com.example.rpgaudiomixer.app.screens.home.HomeScreen
import com.example.rpgaudiomixer.app.screens.library.LibraryScreen
import com.example.rpgaudiomixer.app.screens.scenes.ScenesScreen
import com.example.rpgaudiomixer.app.screens.sessions.SessionsScreen
import com.example.rpgaudiomixer.app.screens.sessionscenes.SessionScenesScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.CAMPAIGNS.name,
        modifier = modifier,
    ) {
        composable(MainNavDestination.HOME.name) {
            HomeScreen(
                onNavigateToSessions = { campaignId ->
                    navController.navigate("sessions/$campaignId")
                },
                onNavigateToCredits = {
                    navController.navigate(MainNavDestination.CREDITS_ROUTE)
                },
            )
        }
        composable(MainNavDestination.CAMPAIGNS.name) {
            Box(modifier = Modifier.testTag("campaignsScreen")) {
                CampaignsScreen(
                    onNavigateToSessions = { campaignId ->
                        navController.navigate("sessions/$campaignId")
                    },
                )
            }
        }
        composable(
            route = MainNavDestination.SESSIONS_ROUTE,
            arguments = listOf(
                navArgument("campaignId") { type = NavType.LongType },
            ),
        ) {
            SessionsScreen(
                onNavigateToSessionScenes = { sessionId ->
                    navController.navigate("sessionScenes/$sessionId")
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCredits = {
                    navController.navigate(MainNavDestination.CREDITS_ROUTE)
                },
            )
        }
        composable(
            route = MainNavDestination.SESSION_SCENES_ROUTE,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.LongType },
            ),
        ) {
            SessionScenesScreen(
                onNavigateToActiveScene = { sceneId, _ ->
                    // future: navigate to active scene playback
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCredits = {
                    navController.navigate(MainNavDestination.CREDITS_ROUTE)
                },
            )
        }
        composable(MainNavDestination.SCENES.name) {
            ScenesScreen(
                onNavigateToActiveScene = { sceneId, _ ->
                    // future: navigate to active scene playback
                },
                onNavigateToCredits = {
                    navController.navigate(MainNavDestination.CREDITS_ROUTE)
                },
            )
        }
        composable(MainNavDestination.LIBRARY.name) {
            LibraryScreen()
        }
        composable(MainNavDestination.CREDITS_ROUTE) {
            CreditsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
