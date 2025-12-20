package com.example.rpgaudiomixer.infra.storage

import android.content.Context
import com.example.rpgaudiomixer.domain.media.TrackNotFoundException
import com.example.rpgaudiomixer.domain.storage.TrackRepository

class LocalTrackRepository(
    private val rawResourceResolver: RawResourceResolver,
    private val assetTrackIndex: AssetTrackIndex,
) : TrackRepository {

    constructor(appContext: Context) : this(
        rawResourceResolver = AndroidRawResourceResolver(appContext),
        assetTrackIndex = AndroidAssetTrackIndex(appContext.assets),
    )

    override fun getTrackFilePath(trackName: String): String {
        // Prefer res/raw. The players already map a raw resource name to android.resource://...
        if (rawResourceResolver.rawResIdOrNull(trackName) != null) return trackName

        // Fallback to assets: app/src/main/assets/tracks/<trackName>.mp3
        val assetPath = "tracks/$trackName.mp3"
        if (assetTrackIndex.exists(assetPath)) return "file:///android_asset/$assetPath"

        throw TrackNotFoundException(
            "Track '$trackName' not found in res/raw or assets/$assetPath."
        )
    }

    override fun getCategoryFolderPath(category: String): String {
        // Categories are currently represented as asset folders.
        return "file:///android_asset/tracks/$category/"
    }
}