package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rpgaudiomixer.app.screens.ActiveSceneScreen
import com.example.rpgaudiomixer.app.screens.AddToSceneScreen
import com.example.rpgaudiomixer.app.screens.CampaignsScreen
import com.example.rpgaudiomixer.app.screens.CreditsScreen
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.app.screens.ScenesScreen
import com.example.rpgaudiomixer.app.screens.SessionScenesScreen
import com.example.rpgaudiomixer.app.screens.SessionsScreen
import com.example.rpgaudiomixer.app.screens.SoundscapeComposerScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onEnterCampaign = { campaignId -> navController.navigate(Routes.sessions(campaignId)) },
                onEnterScene = { sceneId -> navController.navigate(Routes.activeScene(sceneId, false)) },
                onEnterScenePlay = { sceneId -> navController.navigate(Routes.activeScene(sceneId, true)) },
                onCredits = { navController.navigate(Routes.CREDITS) },
            )
        }

        composable(Routes.CAMPAIGNS) {
            CampaignsScreen(
                onEnterCampaign = { campaignId -> navController.navigate(Routes.sessions(campaignId)) },
                onCredits = { navController.navigate(Routes.CREDITS) },
            )
        }

        composable(Routes.SCENES) {
            ScenesScreen(
                onOpenScene = { sceneId -> navController.navigate(Routes.activeScene(sceneId, false)) },
                onPlayScene = { sceneId -> navController.navigate(Routes.activeScene(sceneId, true)) },
                onCredits = { navController.navigate(Routes.CREDITS) },
            )
        }

        composable(Routes.LIBRARY) {
            LibraryScreen(
                onEditCategory = { categoryId -> navController.navigate(Routes.soundscapeComposer(categoryId)) },
                onNewCategory = { navController.navigate(Routes.soundscapeComposer(-1L)) },
                onBack = { navController.popBackStack() },
                onCredits = { navController.navigate(Routes.CREDITS) },
            )
        }

        composable(Routes.CREDITS) {
            CreditsScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.SESSIONS,
            arguments = listOf(navArgument("campaignId") { type = NavType.LongType }),
        ) {
            SessionsScreen(
                onOpenSession = { sessionId -> navController.navigate(Routes.sessionScenes(sessionId)) },
                onBack = { navController.popBackStack() },
                onCredits = { navController.navigate(Routes.CREDITS) },
            )
        }

        composable(
            route = Routes.SESSION_SCENES,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType }),
        ) {
            SessionScenesScreen(
                onOpenScene = { sceneId -> navController.navigate(Routes.activeScene(sceneId, false)) },
                onPlayScene = { sceneId -> navController.navigate(Routes.activeScene(sceneId, true)) },
                onBack = { navController.popBackStack() },
                onCredits = { navController.navigate(Routes.CREDITS) },
            )
        }

        composable(
            route = Routes.ACTIVE_SCENE,
            arguments = listOf(
                navArgument("sceneId") { type = NavType.LongType },
                navArgument("autoPlay") { type = NavType.BoolType; defaultValue = false },
            ),
        ) {
            ActiveSceneScreen(
                onAddSoundscape = { sceneId -> navController.navigate(Routes.addSoundscapeToScene(sceneId)) },
                onAddFX = { sceneId -> navController.navigate(Routes.addFXToScene(sceneId)) },
                onBack = { navController.popBackStack() },
                onCredits = { navController.navigate(Routes.CREDITS) },
            )
        }

        composable(
            route = Routes.SOUNDSCAPE_COMPOSER,
            arguments = listOf(navArgument("categoryId") { type = NavType.LongType; defaultValue = -1L }),
        ) {
            SoundscapeComposerScreen(
                onBack = { navController.popBackStack() },
                onCredits = { navController.navigate(Routes.CREDITS) },
            )
        }

        composable(
            route = Routes.ADD_TO_SCENE,
            arguments = listOf(
                navArgument("sceneId") { type = NavType.LongType },
                navArgument("mode") { type = NavType.StringType },
            ),
        ) {
            AddToSceneScreen(
                onBack = { navController.popBackStack() },
                onConfirm = { navController.popBackStack() },
                onCredits = { navController.navigate(Routes.CREDITS) },
            )
        }
    }
}

