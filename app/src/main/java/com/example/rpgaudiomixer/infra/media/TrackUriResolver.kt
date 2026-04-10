package com.example.rpgaudiomixer.infra.media

import android.content.Context
import android.net.Uri
import androidx.annotation.RawRes
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException

internal object TrackUriResolver {
    fun resolve(track: String, appContext: Context): Uri {
        if ("://" in track) return Uri.parse(track)
        if (track.startsWith("/")) return Uri.parse("file://$track")

        val rawResId = appContext.resources.getIdentifier(track, "raw", appContext.packageName)
        if (rawResId != 0) return rawResourceUri(appContext, rawResId)

        throw TrackNotFoundException(
            "Unable to resolve track '$track'. Provide a full URI or a valid raw resource name.",
        )
    }

    private fun rawResourceUri(
        appContext: Context,
        @RawRes resId: Int,
    ): Uri = Uri.parse("android.resource://${appContext.packageName}/$resId")
}
