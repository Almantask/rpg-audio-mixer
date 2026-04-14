package com.example.rpgaudiomixer.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
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

// Shared X-Axis: lateral (tab-level) navigation
private val tabEnter = slideInHorizontally { it } + fadeIn()
private val tabExit = slideOutHorizontally { -it } + fadeOut()
private val tabPopEnter = slideInHorizontally { -it } + fadeIn()
private val tabPopExit = slideOutHorizontally { it } + fadeOut()

// Shared Z-Axis: drill-down navigation
private val drillEnter = fadeIn() + scaleIn(initialScale = 0.90f)
private val drillExit = fadeOut() + scaleOut(targetScale = 1.10f)
private val drillPopEnter = fadeIn() + scaleIn(initialScale = 1.10f)
private val drillPopExit = fadeOut() + scaleOut(targetScale = 0.90f)

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.CAMPAIGNS.name,
        modifier = modifier,
        // Default: Shared X-Axis for lateral navigation between tabs
        enterTransition = { tabEnter },
        exitTransition = { tabExit },
        popEnterTransition = { tabPopEnter },
        popExitTransition = { tabPopExit },
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
            Box(modifier = Modifier.testTag("campaignsScreen")) {
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

        // Drill-down routes use Shared Z-Axis (fade + scale)
        composable(
            route = MainNavDestination.CREDITS_ROUTE,
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit },
        ) {
            CreditsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTrash = { navController.navigate("trash") }
            )
        }
        composable(
            route = "trash",
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit },
        ) {
            TrashScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "sessions/{campaignId}",
            arguments = listOf(navArgument("campaignId") { type = NavType.LongType }),
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit },
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
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit },
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
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit },
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
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit },
        ) {
            AddSoundscapeScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = "add-fx/{sceneId}",
            arguments = listOf(navArgument("sceneId") { type = NavType.LongType }),
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit },
        ) {
            AddFxScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
