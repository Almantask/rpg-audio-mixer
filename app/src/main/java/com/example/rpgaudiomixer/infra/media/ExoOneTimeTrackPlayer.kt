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
    private val activePlayers = linkedMapOf<Int, ExoPlayer>()
    private var nextPlayerId: Int = 0
    private var latestVolume: Float = 1f

    override fun play() {
        val uri = resolveTrackUri(track)
        val playerId = nextPlayerId++
        val player = ExoPlayer.Builder(appContext).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            volume = latestVolume
            prepare()
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        releasePlayer(playerId, stopFirst = false)
                    }
                }
            })
            play()
        }

        activePlayers[playerId] = player
    }

    override fun pause() {
        activePlayers.values.forEach(ExoPlayer::pause)
    }

    override fun stop() {
        val playerIds = activePlayers.keys.toList()
        playerIds.forEach { playerId ->
            releasePlayer(playerId, stopFirst = true)
        }
    }

    override fun resume() {
        activePlayers.values.forEach(ExoPlayer::play)
    }

    override fun setVolume(volume: Float) {
        latestVolume = volume.coerceIn(0f, 1f)
        activePlayers.values.forEach { player ->
            player.volume = latestVolume
        }
    }

    override val isPlaying: Boolean
        get() = activePlayers.values.any(ExoPlayer::isPlaying)

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

    private fun releasePlayer(playerId: Int, stopFirst: Boolean) {
        activePlayers.remove(playerId)?.let { player ->
            if (stopFirst) {
                player.stop()
            }
            player.release()
        }
    }
}
