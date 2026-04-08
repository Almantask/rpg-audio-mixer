package com.example.rpgaudiomixer.data.soundscape

data class ImportedAudioFile(
    val displayName: String,
    val storedPath: String,
)

interface ImportedAudioStorage {
    suspend fun importAudio(sourceUri: String): ImportedAudioFile
}
