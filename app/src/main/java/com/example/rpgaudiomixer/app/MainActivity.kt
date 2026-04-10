package com.example.rpgaudiomixer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.MainBottomNavBar
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
                var currentTab by rememberSaveable { mutableStateOf(MainNavDestination.HOME) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        ArcanumTopBar(
                            title = getScreenTitle(currentTab),
                            showBackArrow = false,
                            onGearClick = {
                                // TODO: Navigate to Credits screen
                            }
                        )
                    },
                    bottomBar = {
                        MainBottomNavBar(current = currentTab) { dest ->
                            currentTab = dest
                            navController.navigate(dest.name) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
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

    private fun getScreenTitle(destination: MainNavDestination): String {
        return when (destination) {
            MainNavDestination.HOME -> "Arcanum Audio"
            MainNavDestination.CAMPAIGNS -> "Campaigns"
            MainNavDestination.SCENES -> "Scenes"
            MainNavDestination.LIBRARY -> "Audio Library"
        }
    }
}