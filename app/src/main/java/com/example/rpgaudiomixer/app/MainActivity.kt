package com.example.rpgaudiomixer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.MainBottomNavBar
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.navigation.MainNavHost
import com.example.rpgaudiomixer.app.screens.SettingsSyncRepository
import com.example.rpgaudiomixer.app.theme.RPGAudioMixerTheme
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var musicPlayer: MixedMusicPlayer

    @Inject
    lateinit var settingsSyncRepository: SettingsSyncRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RPGAudioMixerTheme {
                MainAppShell(settingsSyncRepository)
            }
        }
    }
}

@Composable
private fun MainAppShell(
    settingsSyncRepository: SettingsSyncRepository,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var selectedRootDestination by rememberSaveable { mutableStateOf(MainNavDestination.HOME) }

    // Defaulting nested or unmatched routes to HOME is safe here because this value only
    // drives the displayed title/back-arrow shell state until ROOT_DESTINATIONS mapping
    // resolves the correct root destination for bottom-navigation selection.
    val activeDestination = destinationByRoute[currentRoute]
        ?: MainNavDestination.HOME

    LaunchedEffect(currentRoute) {
        val rootDestination = rootDestinationForRoute(currentRoute)
        if (rootDestination != null) {
            selectedRootDestination = rootDestination
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ArcanumTopBar(
                title = activeDestination.screenTitle,
                showBackArrow = activeDestination !in ROOT_DESTINATIONS,
                onBack = { navController.popBackStack() },
                onGearClick = {
                    if (currentRoute != MainNavDestination.SETTINGS.route) {
                        navController.navigate(MainNavDestination.SETTINGS.route)
                    }
                },
            )
        },
        bottomBar = {
            MainBottomNavBar(current = selectedRootDestination) { destination ->
                selectedRootDestination = destination
                navController.navigate(destination.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
    ) { innerPadding ->
        MainNavHost(
            navController = navController,
            settingsSyncRepository = settingsSyncRepository,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

private val ROOT_DESTINATIONS = setOf(
    MainNavDestination.HOME,
    MainNavDestination.CAMPAIGNS,
    MainNavDestination.SCENES,
    MainNavDestination.LIBRARY,
)

private val destinationByRoute = MainNavDestination.entries.associateBy { it.route }
private val rootDestinationByRoute = ROOT_DESTINATIONS.associateBy { it.route }

private val childRoutePrefixes = mapOf(
    "campaigns/" to MainNavDestination.CAMPAIGNS,
    "sessions/" to MainNavDestination.CAMPAIGNS,
    "scenes/" to MainNavDestination.SCENES,
)

private fun rootDestinationForRoute(route: String?): MainNavDestination? {
    return rootDestinationByRoute[route] ?: childRoutePrefixes.entries.firstOrNull { (prefix, _) ->
        route?.startsWith(prefix) == true
    }?.value
}
