package com.example.rpgaudiomixer.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.home.HomeScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen
import com.example.rpgaudiomixer.ui.sessions.SessionsScreen
import com.example.rpgaudiomixer.ui.sessionscenes.SessionScenesScreen
import com.example.rpgaudiomixer.ui.soundscapelibrary.SoundscapeLibraryScreen
import com.example.rpgaudiomixer.ui.soundscapecomposer.SoundscapeCategoryComposerScreen
import com.example.rpgaudiomixer.ui.soundscapecomposer.SoundscapeCategoryComposerViewModel

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
                onEnterDomain = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
                onEnterScene = { sceneId ->
                    // TODO: Navigate to active scene
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
        composable(
            route = "campaigns/{campaignId}/sessions",
            arguments = listOf(navArgument("campaignId") { type = NavType.StringType })
        ) {
            SessionsScreen(
                onSessionClick = { sessionId ->
                    navController.navigate("sessions/$sessionId/scenes")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "sessions/{sessionId}/scenes",
            arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
        ) {
            SessionScenesScreen(
                onSceneClick = { sceneId ->
                    // TODO: Navigate to active scene
                }
            )
        }
        composable(MainNavDestination.SCENES.route) {
            ScenesScreen(
                onSceneClick = { sceneId ->
                    // TODO: Navigate to active scene
                }
            )
        }
        composable(MainNavDestination.LIBRARY.route) {
            SoundscapeLibraryScreen(
                onCategoryClick = { categoryId ->
                    navController.navigate("library/soundscapes/$categoryId/compose")
                }
            )
        }
        composable(
            route = "library/soundscapes/{categoryId}/compose",
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            val viewModelFactory: SoundscapeCategoryComposerViewModel.Factory = hiltViewModel()
            SoundscapeCategoryComposerScreen(
                categoryId = categoryId,
                onBackClick = { navController.popBackStack() },
                viewModelFactory = viewModelFactory
            )
        }
    }
}
