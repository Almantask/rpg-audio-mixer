package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.rpgaudiomixer.domain.media.TrackFactory
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import com.example.rpgaudiomixer.domain.media.TrackPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExoTrackFactory @Inject constructor(
    @ApplicationContext private val appContext: Context
) : TrackFactory {

    override fun createLoopableTrackPlayer(track: String): TrackPlayer {
        val uri = resolveTrackUri(track)
        val mediaItem = MediaItem.fromUri(uri)
        val exoPlayer = ExoPlayer.Builder(appContext).build()
        return ExoLoopableTrackPlayer(exoPlayer, mediaItem)
    }

    override fun createOneTimeTrackPlayer(track: String): TrackPlayer {
        val uri = resolveTrackUri(track)
        val mediaItem = MediaItem.fromUri(uri)
        val exoPlayer = ExoPlayer.Builder(appContext).build()
        return ExoOneTimeTrackPlayer(exoPlayer, mediaItem)
    }

    private fun resolveTrackUri(track: String): Uri {
        // If it's already a full URI (file:///android_asset/... or content://...), use it
        if ("://" in track) return Uri.parse(track)

        // If it's a raw resource name, map it to android.resource://...
        val rawResId = appContext.resources.getIdentifier(track, "raw", appContext.packageName)
        if (rawResId != 0) return rawResourceUri(rawResId)

        throw TrackNotFoundException(
            "Unable to resolve track '$track'. Provide a full URI or valid raw resource name."
        )
    }

    private fun rawResourceUri(@RawRes resId: Int): Uri =
        Uri.parse("android.resource://${appContext.packageName}/$resId")
}
