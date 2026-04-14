package com.example.rpgaudiomixer.app.navigation

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.app.screens.activescene.ActiveSceneScreen
import com.example.rpgaudiomixer.app.screens.activescene.AddFxScreen
import com.example.rpgaudiomixer.app.screens.activescene.AddSoundscapeScreen
import com.example.rpgaudiomixer.app.screens.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.app.screens.credits.CreditsScreen
import com.example.rpgaudiomixer.app.screens.home.HomeScreen
import com.example.rpgaudiomixer.app.screens.library.LibraryScreen
import com.example.rpgaudiomixer.app.screens.scenes.ScenesScreen
import com.example.rpgaudiomixer.app.screens.scenes.SessionScenesScreen
import com.example.rpgaudiomixer.app.screens.sessions.SessionsScreen
import com.example.rpgaudiomixer.app.screens.trash.TrashScreen
import androidx.compose.runtime.Composable

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
                onNavigateToCampaignSessions = { campaignId ->
                    navController.navigate("sessions/$campaignId")
                },
                modifier = Modifier.testTag("homeScreen")
            )
        }
        composable(MainNavDestination.CAMPAIGNS.name) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.testTag("campaignsScreen")) {
                CampaignsScreen(
                    onNavigateToSessions = { campaignId ->
                        navController.navigate("sessions/$campaignId")
                    },
                )
            }
        }
        composable(MainNavDestination.SCENES.name) {
            ScenesScreen(
                onNavigateToSessionScenes = { sessionId ->
                    navController.navigate("session-scenes/$sessionId")
                }
            )
        }
        composable(MainNavDestination.LIBRARY.name) {
            LibraryScreen()
        }
        composable(MainNavDestination.CREDITS_ROUTE) {
            CreditsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTrash = { navController.navigate("trash") }
            )
        }
        composable("trash") {
            TrashScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "sessions/{campaignId}",
            arguments = listOf(navArgument("campaignId") { type = NavType.LongType })
        ) {
            SessionsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSessionScenes = { sessionId ->
                    navController.navigate("session-scenes/$sessionId")
                }
            )
        }
        composable(
            route = "session-scenes/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) {
            SessionScenesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToActiveScene = { sceneId ->
                    navController.navigate("active-scene/$sceneId")
                },
                onPlayScene = { sceneId ->
                    navController.navigate("active-scene/$sceneId?autoPlay=true")
                },
            )
        }
        composable(
            route = "active-scene/{sceneId}?autoPlay={autoPlay}",
            arguments = listOf(
                navArgument("sceneId") { type = NavType.LongType },
                navArgument("autoPlay") { type = NavType.BoolType; defaultValue = false },
            ),
        ) { backStackEntry ->
            val sceneId = backStackEntry.arguments?.getLong("sceneId") ?: 0L
            ActiveSceneScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddSoundscape = {
                    navController.navigate("add-soundscape/$sceneId")
                },
                onNavigateToAddFx = {
                    navController.navigate("add-fx/$sceneId")
                },
            )
        }
        composable(
            route = "add-soundscape/{sceneId}",
            arguments = listOf(navArgument("sceneId") { type = NavType.LongType }),
        ) {
            AddSoundscapeScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = "add-fx/{sceneId}",
            arguments = listOf(navArgument("sceneId") { type = NavType.LongType }),
        ) {
            AddFxScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
