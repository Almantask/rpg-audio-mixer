package com.example.rpgaudiomixer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.example.rpgaudiomixer.app.theme.ArcanumTheme
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity for Arcanum Audio
 * Wires together: Theme, Navigation, Top Bar, Bottom Nav
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var musicPlayer: MixedMusicPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArcanumTheme {
                val navController = rememberNavController()
                var currentTab by rememberSaveable { mutableStateOf(MainNavDestination.HOME) }

                // Track current route for back button visibility
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route
                val isCreditsScreen = currentRoute == "credits"

                // Determine top bar title based on current route
                val topBarTitle = when {
                    isCreditsScreen -> "Credits"
                    else -> currentTab.label
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        ArcanumTopBar(
                            title = topBarTitle,
                            showBackArrow = isCreditsScreen,
                            onBackClick = { navController.navigateUp() },
                            onGearClick = { navController.navigate("credits") }
                        )
                    },
                    bottomBar = {
                        // Hide bottom nav on credits screen
                        if (!isCreditsScreen) {
                            MainBottomNavBar(
                                current = currentTab,
                                onNavigate = { destination ->
                                    currentTab = destination
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
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
