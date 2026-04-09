package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.domain.trash.SoundscapeCategoryTrashRepository
import com.example.rpgaudiomixer.domain.trash.TrashVaultRepository
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySoundscapeCategoryTrashRepository @Inject constructor(
    private val trashVaultRepository: TrashVaultRepository,
) : SoundscapeCategoryTrashRepository {
    private val deletedCategories = CopyOnWriteArraySet<String>()

    override fun recordDeletedCategory(name: String) {
        deletedCategories += name
    }

    override fun containsDeletedCategory(name: String): Boolean = name in deletedCategories

    override fun reset() {
        deletedCategories.clear()
        trashVaultRepository.reset()
    }
}
