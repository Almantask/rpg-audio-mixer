package com.example.rpgaudiomixer.app.navigation
 
import com.example.rpgaudiomixer.app.screens.CampaignsScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.rpgaudiomixer.app.screens.HomeScreen

private val lateralEnter = fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 }
private val lateralExit = fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 }
private val lateralPopEnter = fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 }
private val lateralPopExit = fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 4 }

private val drillEnter = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.9f)
private val drillExit = fadeOut(tween(300)) + scaleOut(tween(300), targetScale = 0.9f)
private val drillPopEnter = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 1.1f)
private val drillPopExit = fadeOut(tween(300)) + scaleOut(tween(300), targetScale = 1.1f)

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.HOME.name,
        modifier = modifier
    ) {
        composable(
            MainNavDestination.HOME.name,
            enterTransition = { lateralEnter },
            exitTransition = { lateralExit },
            popEnterTransition = { lateralPopEnter },
            popExitTransition = { lateralPopExit }
        ) {
            val uiState by hiltViewModel<com.example.rpgaudiomixer.app.ui.home.HomeViewModel>().uiState.collectAsState()
            HomeScreen(
                onCampaignClick = { id -> 
                    navController.navigate("campaigns/$id/sessions?name=Active Campaign")
                },
                onResumeClick = { id, autoPlay ->
                    val campaignId = uiState.activeCampaign?.id ?: -1L
                    navController.navigate("active_scene/$id?autoPlay=$autoPlay&campaignId=$campaignId")
                },
                onNavigateToCampaigns = {
                    navController.navigate(MainNavDestination.CAMPAIGNS.name)
                },
                onNavigateToLibrary = {
                    navController.navigate(MainNavDestination.LIBRARY.name)
                }
            )
        }
        composable(
            MainNavDestination.CAMPAIGNS.name,
            enterTransition = { lateralEnter },
            exitTransition = { lateralExit },
            popEnterTransition = { lateralPopEnter },
            popExitTransition = { lateralPopExit }
        ) {
            CampaignsScreen(
                onCampaignClick = { id, name -> 
                    navController.navigate("campaigns/$id/sessions?name=$name")
                }
            )
        }
        composable(
            route = "campaigns/{campaignId}/sessions?name={name}",
            arguments = listOf(
                androidx.navigation.navArgument("campaignId") { type = androidx.navigation.NavType.LongType },
                androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType }
            ),
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit }
        ) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getLong("campaignId") ?: 0L
            val campaignName = backStackEntry.arguments?.getString("name") ?: "Sessions"
            com.example.rpgaudiomixer.app.screens.SessionsScreen(
                campaignName = campaignName,
                onSessionClick = { sessionId ->
                    navController.navigate("sessions/$sessionId/scenes")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "sessions/{sessionId}/scenes",
            arguments = listOf(
                androidx.navigation.navArgument("sessionId") { type = androidx.navigation.NavType.LongType }
            ),
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit }
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            com.example.rpgaudiomixer.app.screens.SessionScenesScreen(
                onSceneClick = { sceneId, autoPlay -> 
                    navController.navigate("active_scene/$sceneId?autoPlay=$autoPlay&sessionId=$sessionId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            MainNavDestination.SCENES.name,
            enterTransition = { lateralEnter },
            exitTransition = { lateralExit },
            popEnterTransition = { lateralPopEnter },
            popExitTransition = { lateralPopExit }
        ) {
            com.example.rpgaudiomixer.app.screens.ScenesScreen(
                onSceneClick = { sceneId, autoPlay -> 
                    navController.navigate("active_scene/$sceneId?autoPlay=$autoPlay")
                }
            )
        }
        composable(
            MainNavDestination.LIBRARY.name,
            enterTransition = { lateralEnter },
            exitTransition = { lateralExit },
            popEnterTransition = { lateralPopEnter },
            popExitTransition = { lateralPopExit }
        ) {
            com.example.rpgaudiomixer.app.screens.LibraryScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate("library/soundscapes/$categoryId/compose")
                }
            )
        }
        composable(
            route = "active_scene/{sceneId}?autoPlay={autoPlay}&sessionId={sessionId}&campaignId={campaignId}",
            arguments = listOf(
                androidx.navigation.navArgument("sceneId") { type = androidx.navigation.NavType.LongType },
                androidx.navigation.navArgument("autoPlay") { 
                    type = androidx.navigation.NavType.BoolType
                    defaultValue = false
                },
                androidx.navigation.navArgument("sessionId") {
                    type = androidx.navigation.NavType.LongType
                    defaultValue = -1L
                },
                androidx.navigation.navArgument("campaignId") {
                    type = androidx.navigation.NavType.LongType
                    defaultValue = -1L
                }
            ),
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit }
        ) { backStackEntry ->
            val sceneId = backStackEntry.arguments?.getLong("sceneId") ?: 0L
            val autoPlay = backStackEntry.arguments?.getBoolean("autoPlay") ?: false
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: -1L
            val campaignId = backStackEntry.arguments?.getLong("campaignId") ?: -1L
            com.example.rpgaudiomixer.app.screens.ActiveSceneScreen(
                sceneId = sceneId,
                autoPlay = autoPlay,
                sessionId = sessionId,
                campaignId = campaignId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "library/soundscapes/{categoryId}/compose",
            arguments = listOf(
                androidx.navigation.navArgument("categoryId") { type = androidx.navigation.NavType.LongType }
            ),
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit }
        ) {
            com.example.rpgaudiomixer.app.screens.ComposerScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "CREDITS",
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit }
        ) {
            com.example.rpgaudiomixer.app.screens.CreditsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToTrash = { navController.navigate("TRASH") }
            )
        }
        composable(
            "TRASH",
            enterTransition = { drillEnter },
            exitTransition = { drillExit },
            popEnterTransition = { drillPopEnter },
            popExitTransition = { drillPopExit }
        ) {
            com.example.rpgaudiomixer.app.screens.TrashScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}


@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Placeholder for $name",
            style = MaterialTheme.typography.displayLarge
        )
    }
}

