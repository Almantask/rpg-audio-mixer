package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.domain.trash.FxTrackTrashRepository
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryFxTrackTrashRepository @Inject constructor() : FxTrackTrashRepository {
    private val deletedTracks = CopyOnWriteArraySet<String>()

    override fun recordDeletedTrack(name: String) {
        deletedTracks += name
    }

    override fun containsDeletedTrack(name: String): Boolean = name in deletedTracks

    override fun reset() {
        deletedTracks.clear()
    }
}
