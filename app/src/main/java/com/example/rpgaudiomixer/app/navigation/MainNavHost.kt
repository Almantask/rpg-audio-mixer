


package com.example.rpgaudiomixer.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rpgaudiomixer.app.screens.HomeScreen
import com.example.rpgaudiomixer.app.screens.CampaignsScreen
import com.example.rpgaudiomixer.app.screens.ScenesScreen
import com.example.rpgaudiomixer.app.screens.LibraryScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rpgaudiomixer.ui.home.HomeViewModel
import com.example.rpgaudiomixer.ui.campaigns.CampaignsViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainNavDestination.HOME.name,
        modifier = modifier
    ) {
        composable(MainNavDestination.HOME.name) {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(viewModel = homeViewModel)
        }
        composable(MainNavDestination.CAMPAIGNS.name) {
            val campaignsViewModel: CampaignsViewModel = viewModel()
            CampaignsScreen(viewModel = campaignsViewModel)
        }
        composable(MainNavDestination.SOUNDBOARD.name) { ScenesScreen() }
        composable(MainNavDestination.SOUNDSCAPES.name) { LibraryScreen() }
    }
}
