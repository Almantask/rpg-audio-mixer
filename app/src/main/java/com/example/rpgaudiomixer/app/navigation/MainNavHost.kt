package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.app.screens.CreditsScreen
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.app.screens.PlaceholderCampaignsScreen
import com.example.rpgaudiomixer.app.screens.PlaceholderScenesScreen
import com.example.rpgaudiomixer.app.screens.SettingsSyncRepository
import com.example.rpgaudiomixer.app.screens.TrashScreen

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
            HomeScreen()
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            PlaceholderCampaignsScreen()
        }
        composable(MainNavDestination.SCENES.route) {
            PlaceholderScenesScreen()
        }
        composable(MainNavDestination.LIBRARY.route) {
            LibraryScreen()
        }
        composable(MainNavDestination.CREDITS.route) {
            CreditsScreen(
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
