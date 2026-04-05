package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import com.example.rpgaudiomixer.domain.media.TrackPlayer

class ExoOneTimeTrackPlayer(
    private val track: String,
    private val appContext: Context,
) : TrackPlayer {

    private val players = mutableListOf<ExoPlayer>()
    private var volume: Float = 1.0f

    override val isPlaying: Boolean
        get() = players.any { it.isPlaying }

    override fun play() {
        val uri = resolveTrackUri(track)
        val player = ExoPlayer.Builder(appContext).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            volume = this@ExoOneTimeTrackPlayer.volume
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        players.remove(this@apply)
                        release()
                    }
                }
            })
            prepare()
        }
        players.add(player)
        player.play()
    }

    override fun pause() {
        players.forEach { it.pause() }
    }

    override fun stop() {
        players.forEach {
            it.stop()
            it.release()
        }
        players.clear()
    }

    override fun resume() {
        players.forEach { it.play() }
    }

    override fun setVolume(volume: Float) {
        this.volume = volume
        players.forEach { it.volume = volume }
    }

    override fun release() {
        stop()
    }


    private fun resolveTrackUri(track: String): Uri {
        if ("://" in track) return Uri.parse(track)

        val rawResId = appContext.resources.getIdentifier(track, "raw", appContext.packageName)
        if (rawResId != 0) return rawResourceUri(rawResId)

        throw TrackNotFoundException(
            "Unable to resolve track '$track'. Provide a full URI (file:///android_asset/...) or a valid raw resource name."
        )
    }

    private fun rawResourceUri(@RawRes resId: Int): Uri =
        Uri.parse("android.resource://${appContext.packageName}/$resId")
}