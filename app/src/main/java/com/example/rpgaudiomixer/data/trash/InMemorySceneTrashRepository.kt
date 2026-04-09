package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.domain.trash.SceneTrashRepository
import com.example.rpgaudiomixer.domain.trash.TrashVaultRepository
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySceneTrashRepository @Inject constructor(
    private val trashVaultRepository: TrashVaultRepository,
) : SceneTrashRepository {
    private val deletedScenes = CopyOnWriteArraySet<String>()

    override fun recordDeletedScene(name: String) {
        deletedScenes += name
    }

    override fun containsDeletedScene(name: String): Boolean = name in deletedScenes

    override fun reset() {
        deletedScenes.clear()
        trashVaultRepository.reset()
    }
}
