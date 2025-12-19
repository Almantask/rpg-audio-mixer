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
import com.example.rpgaudiomixer.domain.media.MusicPlayer
import com.example.rpgaudiomixer.domain.media.SoundId

object SoundboardTestTags {
    fun soundButton(soundId: SoundId) = "soundButton_${soundId.value}"
    const val nowPlayingText = "nowPlayingText"
}

@Composable
fun SoundboardScreen(
    musicPlayer: MusicPlayer,
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
            soundId = SoundId("whip"),
            musicPlayer = musicPlayer,
            onPlayed = { nowPlaying = "Whip" },
        )

        SoundButton(
            label = "Bark",
            soundId = SoundId("bark"),
            musicPlayer = musicPlayer,
            onPlayed = { nowPlaying = "Bark" },
        )

        SoundButton(
            label = "Owl",
            soundId = SoundId("owl"),
            musicPlayer = musicPlayer,
            onPlayed = { nowPlaying = "Owl" },
        )

        val nowPlayingTextValue = nowPlaying?.let { "Now playing: $it" } ?: "Now playing: (none)"
        Text(
            modifier = Modifier.testTag(SoundboardTestTags.nowPlayingText),
            text = nowPlayingTextValue,
        )
    }
}

@Composable
private fun SoundButton(
    label: String,
    soundId: SoundId,
    musicPlayer: MusicPlayer,
    onPlayed: () -> Unit,
) {
    Button(
        modifier = Modifier.testTag(SoundboardTestTags.soundButton(soundId)),
        onClick = {
            musicPlayer.play(soundId)
            onPlayed()
        },
    ) {
        Text(label)
    }
}

