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

    override fun play() {
        val uri = resolveTrackUri(track)

        val player = ExoPlayer.Builder(appContext).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            play()
        }

        // NOTE: This intentionally leaks for now because TrackPlayer currently has only play().
        // A real implementation should add stop()/release() and manage lifecycle.
        @Suppress("UNUSED_VARIABLE")
        val keepAlive = player
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