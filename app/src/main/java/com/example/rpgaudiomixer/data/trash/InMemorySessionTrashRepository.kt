package com.example.rpgaudiomixer.data.trash

import com.example.rpgaudiomixer.domain.trash.SessionTrashRepository
import java.util.concurrent.CopyOnWriteArraySet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySessionTrashRepository @Inject constructor() : SessionTrashRepository {
    private val deletedSessions = CopyOnWriteArraySet<String>()

    override fun recordDeletedSession(name: String) {
        deletedSessions += name
    }

    override fun containsDeletedSession(name: String): Boolean = name in deletedSessions

    override fun reset() {
        deletedSessions.clear()
    }
}
