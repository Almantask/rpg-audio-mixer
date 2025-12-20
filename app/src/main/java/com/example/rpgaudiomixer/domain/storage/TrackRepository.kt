package com.example.rpgaudiomixer.domain.storage

interface TrackRepository {
    fun getTrackFilePath(trackName: String): String
    fun getCategoryFolderPath(category: String): String
}