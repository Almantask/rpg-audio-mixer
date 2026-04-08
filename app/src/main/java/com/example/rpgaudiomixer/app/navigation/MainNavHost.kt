package com.example.rpgaudiomixer.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeLibraryScreen
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeCategoryComposerScreen

/**
 * Main navigation host for Arcanum Audio
 *
 * Routes: HOME, CAMPAIGNS, SCENES, LIBRARY
 */
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
            PlaceholderScreen("Home")
        }

        composable(MainNavDestination.CAMPAIGNS.route) {
            CampaignsScreen(
                onNavigateToCampaignSessions = { campaignId ->
                    // TODO: Navigate to campaign sessions screen when implemented
                },
                onNavigateToSettings = {
                    // TODO: Navigate to settings/credits when implemented
                }
            )
        }

        composable(MainNavDestination.SCENES.route) {
            PlaceholderScreen("Scenes")
        }

        composable(MainNavDestination.LIBRARY.route) {
            SoundscapeLibraryScreen(
                onNavigateToCategoryComposer = { categoryId ->
                    navController.navigate("category_composer/$categoryId")
                },
                onNavigateToSettings = {
                    // TODO: Navigate to settings/credits when implemented
                }
            )
        }

        composable(
            route = "category_composer/{categoryId}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.LongType }
            )
        ) {
            SoundscapeCategoryComposerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$name Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
