package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import com.example.rpgaudiomixer.domain.media.TrackPlayer

class ExoOneTimeTrackPlayer(
    private val track: String,
    private val appContext: Context,
) : TrackPlayer {
    private var player: ExoPlayer? = null
    override val isPlaying: Boolean
        get() = player?.isPlaying == true

    override fun play() {
        val uri = resolveTrackUri(track)

        val activePlayer = player ?: ExoPlayer.Builder(appContext).build().also { created ->
            created.setMediaItem(MediaItem.fromUri(uri))
            created.prepare()
            player = created
        }
        activePlayer.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.stop()
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

    private fun rawResourceUri(@RawRes resId: Int): Uri =
        Uri.parse("android.resource://${appContext.packageName}/$resId")
}
