package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.example.rpgaudiomixer.app.screens.CampaignSessionsScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.app.screens.ScenesScreen
import com.example.rpgaudiomixer.app.screens.SettingsScreen
import com.example.rpgaudiomixer.app.screens.TrashScreen
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.home.HomeScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    musicPlayer: MixedMusicPlayer,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.HOME.route,
        modifier = modifier,
    ) {
        composable(MainNavDestination.HOME.route) {
            HomeScreen(
                musicPlayer = musicPlayer,
                onOpenCampaign = { campaignId, campaignName ->
                    navController.navigate(
                        AppRoute.CampaignSessions.createRoute(campaignId, campaignName),
                    )
                },
            )
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            CampaignsScreen(
                onOpenCampaign = { campaign ->
                    navController.navigate(
                        AppRoute.CampaignSessions.createRoute(campaign.id, campaign.name),
                    )
                },
            )
        }
        composable(MainNavDestination.SCENES.route) {
            ScenesScreen()
        }
        composable(MainNavDestination.LIBRARY.route) {
            LibraryScreen()
        }
        composable(AppRoute.Settings.route) {
            SettingsScreen(
                onOpenTrash = { navController.navigate(AppRoute.Trash.route) },
            )
        }
        composable(AppRoute.Trash.route) {
            TrashScreen()
        }
        composable(
            route = AppRoute.CampaignSessions.route,
            arguments = listOf(
                navArgument("campaignId") { type = NavType.LongType },
                navArgument("campaignName") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) { backStackEntry ->
            CampaignSessionsScreen(
                campaignName = backStackEntry.arguments?.getString("campaignName").orEmpty(),
            )
        }
    }
}
