package com.example.rpgaudiomixer.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.app.screens.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.app.screens.home.HomeScreen
import com.example.rpgaudiomixer.app.screens.library.LibraryScreen
import com.example.rpgaudiomixer.app.screens.library.SoundscapeLibraryScreen
import com.example.rpgaudiomixer.app.screens.library.SoundscapeCategoryComposerScreen
import com.example.rpgaudiomixer.app.screens.campaigns.CampaignSessionsScreen
import com.example.rpgaudiomixer.app.screens.campaigns.SessionScenesScreen
import com.example.rpgaudiomixer.app.screens.scenes.ScenesScreen
import com.example.rpgaudiomixer.app.screens.scenes.ActiveSceneScreen
import com.example.rpgaudiomixer.app.screens.credits.CreditsScreen
import com.example.rpgaudiomixer.app.screens.credits.TrashScreen
import androidx.compose.material3.Text
import com.example.rpgaudiomixer.app.theme.Gold


@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.HOME.name,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(400))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(400))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(400))
        }
    ) {
        composable(MainNavDestination.HOME.name) {
            HomeScreen(
                onNavigateToSessions = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
                onNavigateToActiveScene = { sceneId, autoplay ->
                    // For Home screen resume, we don't have sessionId in the UI state easily 
                    // but we can pass -1 and let VM handle it if needed
                    navController.navigate("scenes/$sceneId?autoplay=$autoplay")
                },
                onNavigateToCredits = {
                    navController.navigate(MainNavDestination.CREDITS.name)
                },
                onNavigateToCampaigns = {
                    navController.navigate(MainNavDestination.CAMPAIGNS.name)
                }
            )
        }
        composable(MainNavDestination.CAMPAIGNS.name) {
            CampaignsScreen(
                onNavigateToSessions = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                }
            )
        }
        composable("campaigns/{campaignId}/sessions") { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId")?.toLong() ?: -1L
            CampaignSessionsScreen(
                campaignId = campaignId,
                onNavigateToScenes = { sessionId ->
                    navController.navigate("sessions/$sessionId/scenes")
                }
            )
        }
        composable("sessions/{sessionId}/scenes") { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLong() ?: -1L
            SessionScenesScreen(
                sessionId = sessionId,
                onOpenScene = { sceneId, autoplay ->
                    navController.navigate("scenes/$sceneId?autoplay=$autoplay&sessionId=$sessionId")
                }
            )
        }
        composable(MainNavDestination.SCENES.name) {
            ScenesScreen(
                onOpenScene = { sceneId, autoplay ->
                    navController.navigate("scenes/$sceneId?autoplay=$autoplay&sessionId=-1")
                }
            )
        }
        composable("scenes/{sceneId}?autoplay={autoplay}&sessionId={sessionId}") { backStackEntry ->
            val sceneId = backStackEntry.arguments?.getString("sceneId")?.toLong() ?: -1L
            val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLong() ?: -1L
            ActiveSceneScreen(
                sceneId = sceneId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(MainNavDestination.LIBRARY.name) {
            LibraryScreen(
                onNavigateToSoundscapeComposer = { categoryId ->
                    navController.navigate("soundscape_composer/$categoryId")
                }
            )
        }
        composable("soundscape_composer/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toLong() ?: -1L
            SoundscapeCategoryComposerScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(MainNavDestination.CREDITS.name) {
            CreditsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToTrash = { navController.navigate(MainNavDestination.TRASH.name) }
            )
        }
        composable(MainNavDestination.TRASH.name) {
            TrashScreen(onBack = { navController.popBackStack() })
        }
    }
}
