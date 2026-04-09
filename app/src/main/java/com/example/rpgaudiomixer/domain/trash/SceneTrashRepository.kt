package com.example.rpgaudiomixer.domain.trash

interface SceneTrashRepository {
    fun recordDeletedScene(name: String)
    fun containsDeletedScene(name: String): Boolean
    fun reset()
}
