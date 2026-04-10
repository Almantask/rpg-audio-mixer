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
import com.example.rpgaudiomixer.ui.library.SoundscapeCategoryComposerRoute
import com.example.rpgaudiomixer.ui.library.SoundscapeLibraryRoute

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
        composable(MainNavDestination.HOME.name) {
            PlaceholderScreen("Home")
        }

        composable(MainNavDestination.CAMPAIGNS.name) {
            CampaignsScreen(
                onNavigateToSessions = { campaignId ->
                    // TODO: Navigate to sessions screen
                }
            )
        }

        composable(MainNavDestination.SCENES.name) {
            PlaceholderScreen("Scenes")
        }

        composable(MainNavDestination.LIBRARY.name) {
            SoundscapeLibraryRoute(
                onNavigateToComposer = { categoryId ->
                    navController.navigate("library/soundscapes/$categoryId/compose")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "library/soundscapes/{categoryId}/compose",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType }
            )
        ) {
            SoundscapeCategoryComposerRoute(
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
            style = MaterialTheme.typography.displayMedium
        )
    }
}
