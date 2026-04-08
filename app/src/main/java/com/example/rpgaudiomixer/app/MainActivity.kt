package com.example.rpgaudiomixer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.MainBottomNavBar
import com.example.rpgaudiomixer.app.navigation.AppChromeStateResolver
import com.example.rpgaudiomixer.app.navigation.AppRoute
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.navigation.MainNavHost
import com.example.rpgaudiomixer.app.theme.RPGAudioMixerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RPGAudioMixerTheme {
                val navController = rememberNavController()
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route
                val chromeState = AppChromeStateResolver.resolve(currentRoute)
                val currentTab = MainNavDestination.fromRoute(currentRoute)
                    ?: if (currentRoute == AppRoute.SOUNDSCAPE_LIBRARY) {
                        MainNavDestination.LIBRARY
                    } else {
                        MainNavDestination.HOME
                    }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        ArcanumTopBar(
                            title = chromeState.title,
                            showBackArrow = chromeState.showBackArrow,
                            onBack = { navController.popBackStack() },
                            onGearClick = { navController.navigate(AppRoute.CREDITS) },
                        )
                    },
                    bottomBar = {
                        if (chromeState.showBottomBar) {
                            MainBottomNavBar(current = currentTab) { destination ->
                                navController.navigate(destination.route) {
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
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
