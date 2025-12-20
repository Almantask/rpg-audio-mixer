package com.example.rpgaudiomixer.infra.storage

import com.example.rpgaudiomixer.domain.storage.TrackRepository

class LocalTrackRepository: TrackRepository {
    override fun getTrackFilePath(trackName: String): String {
        return "file:///android_asset/tracks/$trackName.mp3"
    }

    override fun getCategoryFolderPath(category: String): String {
        return "file:///android_asset/tracks/$category/"
    }
}