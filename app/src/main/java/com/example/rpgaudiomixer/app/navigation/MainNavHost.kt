package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.app.screens.PlaceholderScenesScreen
import com.example.rpgaudiomixer.app.screens.SettingsScreen
import com.example.rpgaudiomixer.app.screens.SettingsSyncRepository
import com.example.rpgaudiomixer.app.screens.TrashScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsRoute
import com.example.rpgaudiomixer.ui.home.HomeRoute
import com.example.rpgaudiomixer.ui.sessions.CampaignSessionsRoute

@Composable
fun MainNavHost(
    navController: NavHostController,
    settingsSyncRepository: SettingsSyncRepository,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.HOME.route,
        modifier = modifier,
    ) {
        composable(MainNavDestination.HOME.route) {
            HomeRoute(
                onOpenCampaign = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
            )
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            CampaignsRoute(
                onOpenSessions = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
            )
        }
        composable(MainNavDestination.SCENES.route) {
            PlaceholderScenesScreen()
        }
        composable(MainNavDestination.LIBRARY.route) {
            LibraryScreen()
        }
        composable(
            route = MainNavDestination.CAMPAIGN_SESSIONS.route,
            arguments = listOf(
                navArgument("campaignId") { type = NavType.StringType },
            ),
        ) {
            CampaignSessionsRoute()
        }
        composable(MainNavDestination.SETTINGS.route) {
            SettingsScreen(
                syncRepository = settingsSyncRepository,
                onRestoreRecentDeletes = {
                    navController.navigate(MainNavDestination.TRASH.route)
                },
            )
        }
        composable(MainNavDestination.TRASH.route) {
            TrashScreen()
        }
    }
}
