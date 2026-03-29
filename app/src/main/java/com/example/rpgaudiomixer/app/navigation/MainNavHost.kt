package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.app.screens.ActiveSceneScreen
import com.example.rpgaudiomixer.app.screens.CampaignSessionsScreen
import com.example.rpgaudiomixer.app.screens.CampaignsScreen
import com.example.rpgaudiomixer.app.screens.CreditsScreen
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.app.screens.SceneComposerScreen
import com.example.rpgaudiomixer.app.screens.ScenesScreen
import com.example.rpgaudiomixer.app.screens.SoundscapeComposerScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = MainNavDestination.HOME.name, modifier = modifier) {
        composable(MainNavDestination.HOME.name) {
            HomeScreen(navController = navController)
        }
        composable(MainNavDestination.CAMPAIGNS.name) {
            CampaignsScreen(navController = navController)
        }
        composable(MainNavDestination.SCENES.name) {
            ScenesScreen(navController = navController)
        }
        composable(MainNavDestination.LIBRARY.name) {
            LibraryScreen(navController = navController)
        }
        composable(MainNavDestination.CREDITS.name) {
            CreditsScreen(navController = navController)
        }
        composable("campaign/{campaignId}") { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getString("campaignId") ?: ""
            CampaignSessionsScreen(navController = navController, campaignId = campaignId)
        }
        composable("scene/{sceneId}") { backStackEntry ->
            val sceneId = backStackEntry.arguments?.getString("sceneId") ?: ""
            ActiveSceneScreen(navController = navController, sceneId = sceneId)
        }
        composable("soundscapeComposer/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            SoundscapeComposerScreen(navController = navController, categoryId = categoryId)
        }
        composable("scenes/compose") {
            SceneComposerScreen(navController = navController)
        }
    }
}
