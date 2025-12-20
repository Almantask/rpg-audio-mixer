package com.example.rpgaudiomixer.infra.storage

/**
 * Minimal abstraction to allow checking for an asset track file from JVM unit tests.
 */
fun interface AssetTrackIndex {
    /**
     * @return true if the asset path exists and is readable.
     */
    fun exists(assetPath: String): Boolean
}

