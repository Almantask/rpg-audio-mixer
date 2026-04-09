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
    private var volume: Float = 1f

    override val isPlaying: Boolean
        get() = players.any(ExoPlayer::isPlaying)

    override fun play() {
        val uri = resolveTrackUri(track)
        val player = ExoPlayer.Builder(appContext).build()
        player.volume = volume
        player.setMediaItem(MediaItem.fromUri(uri))
        player.addListener(
            object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        disposePlayer(player)
                    }
                }
            },
        )
        player.apply {
            prepare()
            play()
        }
        players += player
    }

    override fun pause() {
        players.forEach(ExoPlayer::pause)
    }

    override fun stop() {
        players.toList().forEach { activePlayer ->
            activePlayer.stop()
            disposePlayer(activePlayer)
        }
    }

    override fun resume() {
        players.forEach(ExoPlayer::play)
    }

    override fun setVolume(volume: Float) {
        this.volume = volume.coerceIn(0f, 1f)
        players.forEach { player -> player.volume = this.volume }
    }

    override fun release() {
        stop()
    }

    private fun resolveTrackUri(track: String): Uri {
        // If it's already a full URI (ex: file:///android_asset/... or content://...), just use it.
        if ("://" in track) return Uri.parse(track)

        // If it's a raw resource name (ex: dog_bark), map it to android.resource://...
        val rawResId = appContext.resources.getIdentifier(track, "raw", appContext.packageName)
        if (rawResId != 0) return rawResourceUri(rawResId)

        throw TrackNotFoundException(
            "Unable to resolve track '$track'. Provide a full URI (file:///android_asset/...) or a valid raw resource name."
        )
    }

    private fun disposePlayer(player: ExoPlayer) {
        player.release()
        players.remove(player)
    }

    private fun rawResourceUri(@RawRes resId: Int): Uri =
        Uri.parse("android.resource://${appContext.packageName}/$resId")
}
