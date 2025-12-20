package com.example.rpgaudiomixer.infra.storage

import android.content.res.AssetManager

class AndroidAssetTrackIndex(
    private val assetManager: AssetManager,
) : AssetTrackIndex {

    override fun exists(assetPath: String): Boolean {
        return try {
            assetManager.open(assetPath).use { }
            true
        } catch (_: Exception) {
            false
        }
    }
}

