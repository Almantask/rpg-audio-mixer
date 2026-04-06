package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import com.example.rpgaudiomixer.domain.media.TrackPlayer

class ExoLoopableTrackPlayer(
    private val track: String,
    private val appContext: Context,
) : TrackPlayer {

    private var player: ExoPlayer? = null

    override val isPlaying: Boolean
        get() = player?.isPlaying ?: false

    override fun play() {
        val uri = resolveTrackUri(track)
        val exo = ExoPlayer.Builder(appContext).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            play()
        }
        player?.stop()
        player?.release()
        player = exo
    }

    override fun pause() {
        player?.pause()
    }

    override fun resume() {
        player?.play()
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun setVolume(volume: Float) {
        player?.volume = volume
    }

    override fun release() {
        player?.release()
        player = null
    }

    private fun resolveTrackUri(track: String): Uri {
        if ("://" in track) return Uri.parse(track)
        val rawResId = appContext.resources.getIdentifier(track, "raw", appContext.packageName)
        if (rawResId != 0) return rawResourceUri(rawResId)
        throw TrackNotFoundException(
            "Unable to resolve track '$track'. Provide a full URI or a valid raw resource name."
        )
    }

    private fun rawResourceUri(@RawRes resId: Int): Uri =
        Uri.parse("android.resource://${appContext.packageName}/$resId")
}