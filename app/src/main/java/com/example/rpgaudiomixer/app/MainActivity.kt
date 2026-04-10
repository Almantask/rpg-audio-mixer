package com.example.rpgaudiomixer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.MainBottomNavBar
import com.example.rpgaudiomixer.app.navigation.AppChrome
import com.example.rpgaudiomixer.app.navigation.AppRoute
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.navigation.MainNavHost
import com.example.rpgaudiomixer.app.theme.RPGAudioMixerTheme
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var musicPlayer: MixedMusicPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RPGAudioMixerTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val chrome = AppChrome.fromRoute(currentRoute)
                val currentTab = MainNavDestination.fromRoute(currentRoute) ?: MainNavDestination.HOME

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        ArcanumTopBar(
                            title = chrome.title,
                            showBackArrow = chrome.showBackArrow,
                            onBack = {
                                if (currentRoute?.substringBefore("?") == AppRoute.SoundscapeComposer.route.substringBefore("?")) {
                                    navBackStackEntry?.savedStateHandle?.set(
                                        "composerBackRequestToken",
                                        System.currentTimeMillis(),
                                    )
                                } else {
                                    navController.navigateUp()
                                }
                            },
                            onGearClick = {
                                if (currentRoute != AppRoute.Settings.route) {
                                    navController.navigate(AppRoute.Settings.route) {
                                        launchSingleTop = true
                                    }
                                }
                            },
                        )
                    },
                    bottomBar = {
                        if (chrome.showBottomBar) {
                            MainBottomNavBar(current = currentTab) { dest ->
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    MainNavHost(
                        navController = navController,
                        musicPlayer = musicPlayer,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
