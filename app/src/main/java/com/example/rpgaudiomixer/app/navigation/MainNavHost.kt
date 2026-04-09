package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.app.screens.CampaignSessionsPlaceholderScreen
import com.example.rpgaudiomixer.app.screens.CreditsScreen
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.app.screens.ScenesScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsRoute

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.HOME.route,
        modifier = modifier,
    ) {
        composable(MainNavDestination.HOME.route) {
            HomeScreen()
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            CampaignsRoute(
                onOpenCampaign = { campaignId ->
                    navController.navigate(MainNavDestination.campaignSessionsRoute(campaignId))
                },
            )
        }
        composable(MainNavDestination.SCENES.route) {
            ScenesScreen()
        }
        composable(MainNavDestination.LIBRARY.route) {
            LibraryScreen()
        }
        composable(
            route = MainNavDestination.CAMPAIGN_SESSIONS_ROUTE,
            arguments = listOf(
                navArgument(MainNavDestination.CAMPAIGN_ID_ARG) {
                    type = NavType.LongType
                },
            ),
        ) { backStackEntry ->
            CampaignSessionsPlaceholderScreen(
                campaignId = backStackEntry.arguments?.getLong(MainNavDestination.CAMPAIGN_ID_ARG) ?: 0L,
            )
        }
        composable(MainNavDestination.CREDITS_ROUTE) {
            CreditsScreen()
        }
    }
}
