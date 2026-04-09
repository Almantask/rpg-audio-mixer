package com.example.rpgaudiomixer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.MainBottomNavBar
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
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val backStackEntry = navController.currentBackStackEntryAsState()
                    val currentRoute = backStackEntry.value?.destination?.route
                    val currentDestination = MainNavDestination.fromRoute(currentRoute)
                    val title = when (currentRoute) {
                        MainNavDestination.CREDITS_ROUTE -> "Credits"
                        else -> currentDestination?.title ?: "Arcanum Audio"
                    }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            ArcanumTopBar(
                                title = title,
                                showBackArrow = currentRoute == MainNavDestination.CREDITS_ROUTE,
                                onBack = { navController.popBackStack() },
                                onGearClick = {
                                    if (currentRoute != MainNavDestination.CREDITS_ROUTE) {
                                        navController.navigate(MainNavDestination.CREDITS_ROUTE)
                                    }
                                },
                            )
                        },
                        bottomBar = {
                            MainBottomNavBar(current = currentDestination) { dest ->
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    ) { innerPadding ->
                        MainNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}
