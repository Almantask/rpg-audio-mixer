package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.app.screens.SettingsScreen
import com.example.rpgaudiomixer.app.screens.SettingsSyncRepository
import com.example.rpgaudiomixer.app.screens.TrashScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsRoute
import com.example.rpgaudiomixer.ui.home.HomeRoute
import com.example.rpgaudiomixer.ui.sessions.CampaignSessionsRoute
import com.example.rpgaudiomixer.ui.scenes.ActiveSceneRoute
import com.example.rpgaudiomixer.ui.scenes.ScenesRoute
import com.example.rpgaudiomixer.ui.sessionscenes.SessionScenesRoute
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeCategoryComposerRoute
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeLibraryRoute

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
            ScenesRoute(
                onOpenScene = { sceneId, autoplay ->
                    navController.navigate("scenes/$sceneId/$autoplay")
                },
            )
        }
        composable(MainNavDestination.LIBRARY.route) {
            SoundscapeLibraryRoute(
                onOpenComposer = { categoryId ->
                    navController.navigate("library/soundscapes/$categoryId/compose")
                },
            )
        }
        composable(
            route = MainNavDestination.CAMPAIGN_SESSIONS.route,
            arguments = listOf(
                navArgument("campaignId") { type = NavType.StringType },
            ),
        ) {
            CampaignSessionsRoute(
                onOpenSessionScenes = { sessionId ->
                    navController.navigate("sessions/$sessionId/scenes")
                },
            )
        }
        composable(
            route = MainNavDestination.SESSION_SCENES.route,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType },
            ),
        ) {
            SessionScenesRoute(
                onOpenScene = { sceneId, autoplay ->
                    navController.navigate("scenes/$sceneId/$autoplay")
                },
            )
        }
        composable(
            route = MainNavDestination.ACTIVE_SCENE.route,
            arguments = listOf(
                navArgument("sceneId") { type = NavType.StringType },
                navArgument("autoplay") { type = NavType.StringType },
            ),
        ) {
            ActiveSceneRoute()
        }
        composable(
            route = MainNavDestination.SOUNDSCAPE_COMPOSER.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
            ),
        ) {
            SoundscapeCategoryComposerRoute(
                onNavigateBack = { navController.popBackStack() },
            )
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
