package com.example.rpgaudiomixer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.rpgaudiomixer.app.di.ServiceLocator
import com.example.rpgaudiomixer.app.soundboard.SoundboardScreen
import com.example.rpgaudiomixer.app.theme.RPGAudioMixerTheme
import com.example.rpgaudiomixer.infra.media.AndroidMusicPlayer

class MainActivity : ComponentActivity() {

    private val musicPlayer by lazy(LazyThreadSafetyMode.NONE) { ServiceLocator.musicPlayerFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RPGAudioMixerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SoundboardScreen(
                        musicPlayer = musicPlayer,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SoundboardPreview() {
    RPGAudioMixerTheme {
        SoundboardScreen(
            musicPlayer = AndroidMusicPlayer(),
        )
    }
}