package com.example.rpgaudiomixer.app.soundboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer

object SoundboardTestTags {
    fun soundButton(soundId: String) = "soundButton_${soundId}"
    const val NOW_PLAYING_TEXT = "nowPlayingText"
}

@Composable
fun SoundboardScreen(
    mixedMusicPlayer: MixedMusicPlayer,
    modifier: Modifier = Modifier,
) {
    var nowPlaying by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Soundboard",
            style = MaterialTheme.typography.headlineSmall,
        )

        SoundButton(
            label = "Whip",
            soundId = "whip",
            mixedMusicPlayer = mixedMusicPlayer,
            onPlayed = { nowPlaying = "Whip" },
        )

        SoundButton(
            label = "Bark",
            soundId = "dog_bark",
            mixedMusicPlayer = mixedMusicPlayer,
            onPlayed = { nowPlaying = "Bark" },
        )

        SoundButton(
            label = "Owl",
            soundId = "owl_hooting",
            mixedMusicPlayer = mixedMusicPlayer,
            onPlayed = { nowPlaying = "Owl" },
        )

        val nowPlayingTextValue = nowPlaying?.let { "Now playing: $it" } ?: "Now playing: (none)"
        Text(
            modifier = Modifier.testTag(SoundboardTestTags.NOW_PLAYING_TEXT),
            text = nowPlayingTextValue,
        )
    }
}

@Composable
private fun SoundButton(
    label: String,
    soundId: String,
    mixedMusicPlayer: MixedMusicPlayer,
    onPlayed: () -> Unit,
) {
    Button(
        modifier = Modifier.testTag(SoundboardTestTags.soundButton(soundId)),
        onClick = {
            mixedMusicPlayer.playSingleSound(soundId)
            onPlayed()
        },
    ) {
        Text(label)
    }
}

