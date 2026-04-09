package com.example.rpgaudiomixer.domain.trash

interface SoundscapeCategoryTrashRepository {
    fun recordDeletedCategory(name: String)
    fun containsDeletedCategory(name: String): Boolean
    fun reset()
}
