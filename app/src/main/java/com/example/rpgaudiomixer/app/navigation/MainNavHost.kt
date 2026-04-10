package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.app.screens.SettingsScreen
import com.example.rpgaudiomixer.app.screens.TrashScreen
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.home.HomeScreen
import com.example.rpgaudiomixer.ui.scenes.ActiveSceneScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen
import com.example.rpgaudiomixer.ui.sessions.CampaignSessionsScreen
import com.example.rpgaudiomixer.ui.sessions.SessionScenesScreen

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
            ScenesScreen(
                onOpenScene = { scene, autoplay ->
                    navController.navigate(
                        AppRoute.ActiveScene.createRoute(
                            sceneId = scene.id,
                            sceneName = scene.name,
                            autoplay = autoplay,
                        ),
                    )
                },
            )
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
                onOpenSession = { session ->
                    navController.navigate(
                        AppRoute.SessionScenes.createRoute(
                            sessionId = session.id,
                            sessionName = session.name,
                        ),
                    )
                },
            )
        }
        composable(
            route = AppRoute.SessionScenes.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.LongType },
                navArgument("sessionName") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            SessionScenesScreen(
                onOpenScene = { scene, autoplay ->
                    navController.navigate(
                        AppRoute.ActiveScene.createRoute(
                            sceneId = scene.id,
                            sceneName = scene.name,
                            autoplay = autoplay,
                        ),
                    )
                },
            )
        }
        composable(
            route = AppRoute.ActiveScene.route,
            arguments = listOf(
                navArgument("sceneId") { type = NavType.LongType },
                navArgument("sceneName") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("autoplay") {
                    type = NavType.BoolType
                    defaultValue = false
                },
            ),
        ) { backStackEntry ->
            ActiveSceneScreen(
                sceneName = backStackEntry.arguments?.getString("sceneName").orEmpty(),
                autoplay = backStackEntry.arguments?.getBoolean("autoplay") == true,
            )
        }
    }
}
