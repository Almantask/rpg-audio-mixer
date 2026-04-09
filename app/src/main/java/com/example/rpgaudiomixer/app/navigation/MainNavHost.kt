package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.rpgaudiomixer.app.screens.CampaignsScreen
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import com.example.rpgaudiomixer.app.screens.ScenesScreen
import com.example.rpgaudiomixer.app.screens.SettingsScreen
import com.example.rpgaudiomixer.app.screens.TrashScreen
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer

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
            HomeScreen(musicPlayer = musicPlayer)
        }
        composable(MainNavDestination.CAMPAIGNS.route) {
            CampaignsScreen()
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
    }
}
