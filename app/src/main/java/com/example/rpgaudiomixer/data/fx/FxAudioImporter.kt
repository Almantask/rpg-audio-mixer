package com.example.rpgaudiomixer.data.fx

data class ImportedFxAudioFile(
    val displayName: String,
    val storedPath: String,
    val durationMs: Long,
)

interface FxAudioImporter {
    suspend fun importAudio(sourceUri: String): ImportedFxAudioFile
}
