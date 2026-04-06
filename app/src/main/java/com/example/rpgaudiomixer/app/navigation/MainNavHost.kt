package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.ui.campaigns.CampaignsScreen
import com.example.rpgaudiomixer.ui.home.HomeScreen
import com.example.rpgaudiomixer.ui.library.LibraryScreen
import com.example.rpgaudiomixer.ui.scenes.ScenesScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.HOME.name,
        modifier = modifier,
    ) {
        composable(MainNavDestination.HOME.name) {
            HomeScreen(
                onEnterCampaign = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
            )
        }
        composable(MainNavDestination.CAMPAIGNS.name) {
            CampaignsScreen(
                onCampaignSelected = { campaignId ->
                    navController.navigate("campaigns/$campaignId/sessions")
                },
            )
        }
        composable(MainNavDestination.SCENES.name) {
            ScenesScreen(
                onSceneSelected = { sceneId ->
                    navController.navigate("scenes/$sceneId/play")
                },
            )
        }
        composable(MainNavDestination.LIBRARY.name) {
            LibraryScreen()
        }
    }
}
