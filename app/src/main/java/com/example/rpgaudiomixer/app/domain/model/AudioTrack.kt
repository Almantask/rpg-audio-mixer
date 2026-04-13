package com.example.rpgaudiomixer.app.domain.model

data class AudioTrack(
    val id: Long = 0,
    val name: String,
    val localPath: String,
    val originalUri: String,
    val type: AudioTrackType,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null
)

enum class AudioTrackType {
    SOUNDSCAPE,
    FX
}
