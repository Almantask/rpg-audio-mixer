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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.MainBottomNavBar
import com.example.rpgaudiomixer.app.data.trash.TrashPurgeManager
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.app.navigation.MainNavHost
import com.example.rpgaudiomixer.app.theme.RPGAudioMixerTheme
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var musicPlayer: MixedMusicPlayer

    @Inject
    lateinit var trashPurgeManager: TrashPurgeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch(Dispatchers.IO) {
            trashPurgeManager.purgeExpired()
        }

        setContent {
            RPGAudioMixerTheme {
                val navController = rememberNavController()
                var currentTab by rememberSaveable { mutableStateOf(MainNavDestination.CAMPAIGNS) }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val isTabScreen = MainNavDestination.entries.any { it.name == currentRoute }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (isTabScreen) {
                            ArcanumTopBar(
                                title = currentTab.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                onGearClick = {
                                    navController.navigate(MainNavDestination.CREDITS_ROUTE)
                                },
                            )
                        }
                    },
                    bottomBar = {
                        if (isTabScreen) {
                            MainBottomNavBar(current = currentTab) { dest ->
                                currentTab = dest
                                navController.navigate(dest.name) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
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
