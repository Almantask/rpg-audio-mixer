package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.ui.activescene.ActiveSceneScreen
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.credits.CreditsScreen
import com.example.rpgaudiomixer.ui.home.HomeScreen
import com.example.rpgaudiomixer.ui.library.LibraryScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen
import com.example.rpgaudiomixer.ui.sessionscenes.AddSoundscapeToSceneScreen
import com.example.rpgaudiomixer.ui.sessionscenes.AddFxToSceneScreen
import com.example.rpgaudiomixer.ui.sessionscenes.SessionScenesScreen
import com.example.rpgaudiomixer.ui.sessions.SessionsScreen
import com.example.rpgaudiomixer.ui.soundscapecomposer.SoundscapeComposerScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier,
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onEnterCampaign = { campaignId ->
                    navController.navigate(NavRoutes.sessions(campaignId))
                },
                onEnterScene = { sceneId ->
                    navController.navigate(NavRoutes.activeScene(sceneId))
                },
                onCredits = { navController.navigate(NavRoutes.CREDITS) },
            )
        }

        composable(NavRoutes.CAMPAIGNS) {
            CampaignsScreen(
                onOpenCampaign = { campaignId ->
                    navController.navigate(NavRoutes.sessions(campaignId))
                },
                onCredits = { navController.navigate(NavRoutes.CREDITS) },
            )
        }

        composable(
            route = NavRoutes.SESSIONS,
            arguments = listOf(navArgument("campaignId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val campaignId = backStackEntry.arguments!!.getLong("campaignId")
            SessionsScreen(
                campaignId = campaignId,
                onOpenSession = { sessionId ->
                    navController.navigate(NavRoutes.sessionScenes(sessionId))
                },
                onBack = { navController.popBackStack() },
                onCredits = { navController.navigate(NavRoutes.CREDITS) },
            )
        }

        composable(
            route = NavRoutes.SESSION_SCENES,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments!!.getLong("sessionId")
            SessionScenesScreen(
                sessionId = sessionId,
                onOpenScene = { sceneId ->
                    navController.navigate(NavRoutes.activeScene(sceneId))
                },
                onBack = { navController.popBackStack() },
                onCredits = { navController.navigate(NavRoutes.CREDITS) },
            )
        }

        composable(
            route = NavRoutes.ACTIVE_SCENE,
            arguments = listOf(navArgument("sceneId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val sceneId = backStackEntry.arguments!!.getLong("sceneId")
            ActiveSceneScreen(
                sceneId = sceneId,
                onBack = { navController.popBackStack() },
                onCredits = { navController.navigate(NavRoutes.CREDITS) },
                onAddSoundscape = { navController.navigate(NavRoutes.addSoundscapeToScene(sceneId)) },
                onAddFx = { navController.navigate(NavRoutes.addFxToScene(sceneId)) },
            )
        }

        composable(NavRoutes.SCENES) {
            ScenesScreen(
                onOpenScene = { sceneId ->
                    navController.navigate(NavRoutes.activeScene(sceneId))
                },
                onCredits = { navController.navigate(NavRoutes.CREDITS) },
            )
        }

        composable(NavRoutes.LIBRARY) {
            LibraryScreen(
                onOpenComposer = { categoryId ->
                    navController.navigate(NavRoutes.soundscapeComposer(categoryId))
                },
                onCredits = { navController.navigate(NavRoutes.CREDITS) },
            )
        }

        composable(
            route = NavRoutes.SOUNDSCAPE_COMPOSER,
            arguments = listOf(navArgument("categoryId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments!!.getLong("categoryId")
            SoundscapeComposerScreen(
                categoryId = categoryId,
                onBack = { navController.popBackStack() },
                onCredits = { navController.navigate(NavRoutes.CREDITS) },
            )
        }

        composable(
            route = NavRoutes.ADD_SOUNDSCAPE_TO_SCENE,
            arguments = listOf(navArgument("sceneId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val sceneId = backStackEntry.arguments!!.getLong("sceneId")
            AddSoundscapeToSceneScreen(
                sceneId = sceneId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = NavRoutes.ADD_FX_TO_SCENE,
            arguments = listOf(navArgument("sceneId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val sceneId = backStackEntry.arguments!!.getLong("sceneId")
            AddFxToSceneScreen(
                sceneId = sceneId,
                onBack = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.CREDITS) {
            CreditsScreen(onBack = { navController.popBackStack() })
        }
    }
}
