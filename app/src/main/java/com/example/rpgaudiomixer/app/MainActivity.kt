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
import com.example.rpgaudiomixer.app.theme.RPGAudioMixerTheme
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

@AndroidEntryPoint

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var musicPlayer: MixedMusicPlayer

    @Inject lateinit var campaignRepo: com.example.rpgaudiomixer.domain.repository.CampaignRepository
    @Inject lateinit var sessionRepo: com.example.rpgaudiomixer.domain.repository.SessionRepository
    @Inject lateinit var sceneRepo: com.example.rpgaudiomixer.domain.repository.SceneRepository
    @Inject lateinit var soundscapeRepo: com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
    @Inject lateinit var fxRepo: com.example.rpgaudiomixer.domain.repository.FXRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Clean up items older than 7 days
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val threshold = System.currentTimeMillis() - java.util.concurrent.TimeUnit.DAYS.toMillis(7)
                campaignRepo.purgeOldDeleted(threshold)
                sessionRepo.purgeOldDeleted(threshold)
                sceneRepo.purgeOldDeleted(threshold)
                soundscapeRepo.purgeOldDeletedCategories(threshold)
                fxRepo.purgeOldDeleted(threshold)
            }
        }

        enableEdgeToEdge()
        setContent {
            RPGAudioMixerTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Determine if we are on a top-level destination
                val currentDestination = rememberSaveable(currentRoute) {
                    try {
                        MainNavDestination.valueOf(currentRoute ?: MainNavDestination.HOME.name)
                    } catch (e: IllegalArgumentException) {
                        MainNavDestination.HOME
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        ArcanumTopBar(
                            title = when (currentDestination) {
                                MainNavDestination.HOME -> "ARCANUM AUDIO"
                                MainNavDestination.CAMPAIGNS -> "CAMPAIGNS"
                                MainNavDestination.SCENES -> "SCENES"
                                MainNavDestination.LIBRARY -> "AUDIO LIBRARY"
                                MainNavDestination.CREDITS -> "BEHIND THE SCREEN"
                                MainNavDestination.TRASH -> "VAULT OF ECHOES"
                                MainNavDestination.ACTIVE_SCENE -> "ACTIVE SCENE"
                            },
                            showBackArrow = currentDestination != MainNavDestination.HOME,
                            onBack = { navController.popBackStack() },
                            onGearClick = {
                                if (currentDestination != MainNavDestination.CREDITS) {
                                    navController.navigate(MainNavDestination.CREDITS.name)
                                }
                            }
                        )
                    },

                    bottomBar = {
                        // Only show bottom nav on main categories
                        if (currentDestination != MainNavDestination.CREDITS) {
                            MainBottomNavBar(current = currentDestination) { dest ->
                                if (currentRoute != dest.name) {
                                    navController.navigate(dest.name) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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