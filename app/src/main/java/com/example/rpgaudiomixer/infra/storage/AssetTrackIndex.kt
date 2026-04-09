package com.example.rpgaudiomixer.infra.storage

fun interface AssetTrackIndex {
    fun exists(assetPath: String): Boolean
}
