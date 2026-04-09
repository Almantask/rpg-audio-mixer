package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.app.screens.CreditsScreen
import com.example.rpgaudiomixer.app.screens.TrashScreen
import com.example.rpgaudiomixer.ui.library.LibraryRoute
import com.example.rpgaudiomixer.ui.library.LibraryTab
import com.example.rpgaudiomixer.ui.campaigns.CampaignsRoute
import com.example.rpgaudiomixer.ui.home.HomeRoute
import com.example.rpgaudiomixer.ui.scenes.ActiveSceneRoute
import com.example.rpgaudiomixer.ui.scenes.ScenesRoute
import com.example.rpgaudiomixer.ui.sessions.CampaignSessionsRoute
import com.example.rpgaudiomixer.ui.sessionscenes.SessionScenesRoute
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeCategoryComposerRoute

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
            HomeRoute(
                onOpenCampaign = { campaignId ->
                    navController.navigate(AppRoute.campaignSessions(campaignId))
                },
                onOpenResumeScene = { sceneId ->
                    navController.navigate(AppRoute.sceneDetails(sceneId, true))
                },
            )
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            CampaignsRoute(
                onOpenCampaign = { campaignId ->
                    navController.navigate(AppRoute.campaignSessions(campaignId))
                },
            )
        }
        composable(MainNavDestination.SCENES.route) {
            ScenesRoute(
                onOpenScene = { sceneId, autoplay ->
                    navController.navigate(AppRoute.sceneDetails(sceneId, autoplay))
                },
            )
        }
        composable(MainNavDestination.LIBRARY.route) {
            LibraryRoute(
                onOpenComposer = { categoryId ->
                    navController.navigate(AppRoute.soundscapeCategoryComposer(categoryId))
                },
            )
        }
        composable(AppRoute.SOUNDSCAPE_LIBRARY) {
            LibraryRoute(
                onOpenComposer = { categoryId ->
                    navController.navigate(AppRoute.soundscapeCategoryComposer(categoryId))
                },
                initialTab = LibraryTab.SOUNDSCAPES,
            )
        }
        composable(
            route = AppRoute.CAMPAIGN_SESSIONS,
            arguments = listOf(
                navArgument(AppRoute.CAMPAIGN_ID_ARG) {
                    type = NavType.LongType
                },
            ),
        ) {
            CampaignSessionsRoute(
                onOpenSession = { sessionId ->
                    navController.navigate(AppRoute.sessionScenes(sessionId))
                },
            )
        }
        composable(
            route = AppRoute.SESSION_SCENES,
            arguments = listOf(
                navArgument(AppRoute.SESSION_ID_ARG) {
                    type = NavType.LongType
                },
            ),
        ) {
            SessionScenesRoute(
                onOpenScene = { sceneId, autoplay ->
                    navController.navigate(AppRoute.sceneDetails(sceneId, autoplay))
                },
            )
        }
        composable(
            route = AppRoute.SCENE_DETAILS,
            arguments = listOf(
                navArgument(AppRoute.SCENE_ID_ARG) {
                    type = NavType.LongType
                },
                navArgument(AppRoute.AUTOPLAY_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                },
            ),
        ) {
            ActiveSceneRoute(
                onOpenSoundscapeComposer = { categoryId ->
                    navController.navigate(AppRoute.soundscapeCategoryComposer(categoryId))
                },
            )
        }
        composable(
            route = AppRoute.SOUNDSCAPE_CATEGORY_COMPOSER,
            arguments = listOf(
                navArgument(AppRoute.SOUNDSCAPE_CATEGORY_ID_ARG) {
                    type = NavType.LongType
                },
            ),
        ) {
            SoundscapeCategoryComposerRoute(
                onNavigateBack = { navController.popBackStack() },
            )
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
