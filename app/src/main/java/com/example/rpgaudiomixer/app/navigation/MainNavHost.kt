package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.example.rpgaudiomixer.app.screens.CreditsScreen
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.app.screens.ScenesScreen
import com.example.rpgaudiomixer.app.screens.TrashScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsRoute
import com.example.rpgaudiomixer.ui.sessions.CampaignSessionsRoute

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
                    navController.navigate(AppRoute.campaignSessions(campaignId))
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
            route = AppRoute.CAMPAIGN_SESSIONS,
            arguments = listOf(
                navArgument(AppRoute.CAMPAIGN_ID_ARG) {
                    type = NavType.LongType
                },
            ),
        ) {
            CampaignSessionsRoute()
        }
        composable(AppRoute.CREDITS) {
            CreditsScreen(
                onOpenTrash = { navController.navigate(AppRoute.TRASH) },
            )
        }
        composable(AppRoute.TRASH) {
            TrashScreen()
        }
    }
}
