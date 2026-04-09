package com.example.rpgaudiomixer.domain.trash

interface SessionTrashRepository {
    fun recordDeletedSession(name: String)
    fun containsDeletedSession(name: String): Boolean
    fun reset()
}
