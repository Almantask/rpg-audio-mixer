package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import java.util.concurrent.CopyOnWriteArrayList

class ExoOneTimeTrackPlayer(
    private val track: String,
    private val appContext: Context,
) : TrackPlayer {

    private val activePlayers = CopyOnWriteArrayList<ExoPlayer>()
    private var volume: Float = 1f
    
    companion object {
        private const val MAX_CONCURRENT_INSTANCES = 5
    }

    override fun play() {
        if (activePlayers.size >= MAX_CONCURRENT_INSTANCES) {
            activePlayers.firstOrNull()?.let {
                it.stop()
                it.release()
                activePlayers.remove(it)
            }
        }
        val uri = resolveTrackUri(track)
        val player = ExoPlayer.Builder(appContext).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            volume = this@ExoOneTimeTrackPlayer.volume
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        activePlayers.remove(this@apply)
                        release()
                    }
                }
            })
        }
        activePlayers.add(player)
        player.play()
    }

    override fun pause() {
        activePlayers.forEach { it.pause() }
    }

    override fun stop() {
        activePlayers.forEach { 
            it.stop()
            it.release()
        }
        activePlayers.clear()
    }

    override fun resume() {
        activePlayers.forEach { it.play() }
    }

    override fun setVolume(volume: Float) {
        this.volume = volume
        activePlayers.forEach { it.volume = volume }
    }

    override val isPlaying: Boolean
        get() = activePlayers.any { it.isPlaying }

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