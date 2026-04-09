package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.domain.trash.SoundscapeCategoryTrashRepository
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySoundscapeCategoryTrashRepository @Inject constructor() : SoundscapeCategoryTrashRepository {
    private val deletedCategories = CopyOnWriteArraySet<String>()

    override fun recordDeletedCategory(name: String) {
        deletedCategories += name
    }

    override fun containsDeletedCategory(name: String): Boolean = name in deletedCategories

    override fun reset() {
        deletedCategories.clear()
    }
}
