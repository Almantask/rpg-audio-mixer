package com.example.rpgaudiomixer.infra.media

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackPlayer

class ExoLoopableTrackPlayer(
    private val track: String,
    private val appContext: Context,
) : TrackPlayer {
    private var player: ExoPlayer? = null
    private var prepared = false

    override val isPlaying: Boolean
        get() = player?.isPlaying == true

    override fun play() {
        val exoPlayer = obtainPlayer()
        if (!prepared) {
            exoPlayer.setMediaItem(MediaItem.fromUri(TrackUriResolver.resolve(track, appContext)))
            exoPlayer.prepare()
            prepared = true
        }
        exoPlayer.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.stop()
        prepared = false
    }

    override fun resume() {
        player?.play()
    }

    override fun setVolume(volume: Float) {
        player?.volume = volume.coerceIn(0f, 1f)
    }

    override fun release() {
        player?.release()
        player = null
        prepared = false
    }

    private fun obtainPlayer(): ExoPlayer {
        return player ?: ExoPlayer.Builder(appContext).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }.also { createdPlayer ->
            player = createdPlayer
        }
    }
}
