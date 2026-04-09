package com.example.rpgaudiomixer.domain.trash

interface FxTrackTrashRepository {
    fun recordDeletedTrack(name: String)
    fun containsDeletedTrack(name: String): Boolean
    fun reset()
}
