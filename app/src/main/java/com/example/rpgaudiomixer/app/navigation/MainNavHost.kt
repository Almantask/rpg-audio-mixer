package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.app.screens.CreditsScreen
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsRoute
import com.example.rpgaudiomixer.ui.campaignsessions.CampaignSessionsRoute
import com.example.rpgaudiomixer.ui.scenes.ActiveScenePlaceholderRoute
import com.example.rpgaudiomixer.ui.scenes.ScenesRoute
import com.example.rpgaudiomixer.ui.sessionscenes.SessionScenesRoute

@Composable
fun MainNavHost(
    navController: NavHostController,
    onTitleChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
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
            ScenesRoute(
                onOpenScene = { sceneId, autoplay ->
                    navController.navigate(
                        MainNavDestination.activeSceneRoute(
                            sceneId = sceneId,
                            autoplay = autoplay,
                        ),
                    )
                },
                onTitleChange = onTitleChange,
            )
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
        ) {
            CampaignSessionsRoute(
                onOpenSession = { sessionId ->
                    navController.navigate(MainNavDestination.sessionScenesRoute(sessionId))
                },
                onTitleChange = onTitleChange,
            )
        }
        composable(
            route = MainNavDestination.SESSION_SCENES_ROUTE,
            arguments = listOf(
                navArgument(MainNavDestination.SESSION_ID_ARG) {
                    type = NavType.LongType
                },
            ),
        ) {
            SessionScenesRoute(
                onOpenScene = { sceneId, autoplay ->
                    navController.navigate(
                        MainNavDestination.activeSceneRoute(
                            sceneId = sceneId,
                            autoplay = autoplay,
                        ),
                    )
                },
                onTitleChange = onTitleChange,
            )
        }
        composable(
            route = MainNavDestination.ACTIVE_SCENE_ROUTE,
            arguments = listOf(
                navArgument(MainNavDestination.SCENE_ID_ARG) {
                    type = NavType.LongType
                },
                navArgument(MainNavDestination.AUTOPLAY_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                },
            ),
        ) {
            ActiveScenePlaceholderRoute(
                onTitleChange = onTitleChange,
            )
        }
        composable(MainNavDestination.CREDITS_ROUTE) {
            CreditsScreen()
        }
    }
}
