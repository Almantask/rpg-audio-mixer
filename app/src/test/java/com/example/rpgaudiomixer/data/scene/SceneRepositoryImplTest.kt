package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.data.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.Scene
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneRepositoryImplTest {

    private val sceneDao = FakeSceneDao()
    private val sessionSceneDao = FakeSessionSceneDao()
    private val repository = SceneRepositoryImpl(
        sceneDao = sceneDao,
        sessionSceneDao = sessionSceneDao,
    )

    @Test
    fun observeScenes_maps_entities_to_domain_models() = runTest {
        // Arrange
        sceneDao.emitScenes(
            listOf(
                SceneEntity(
                    id = 2L,
                    name = "Forest",
                    description = "Dark woods",
                    tags = "forest, travel",
                ),
                SceneEntity(
                    id = 1L,
                    name = "Tavern",
                    description = null,
                    tags = "",
                ),
            )
        )

        // Act
        val result = repository.observeScenes().first()

        // Assert
        assertThat(result).containsExactly(
            Scene(
                id = 2L,
                name = "Forest",
                description = "Dark woods",
                tags = listOf("forest", "travel"),
                soundscapeCount = 0,
            ),
            Scene(
                id = 1L,
                name = "Tavern",
                description = null,
                tags = emptyList(),
                soundscapeCount = 0,
            ),
        )
    }

    @Test
    fun createScene_persists_trimmed_tags_as_csv() = runTest {
        // Arrange
        val tags = listOf(" tavern ", "roleplay", "")

        // Act
        repository.createScene(
            name = "Tavern",
            description = "Warm lights",
            tags = tags,
        )

        // Assert
        assertThat(sceneDao.upsertedScenes).containsExactly(
            SceneEntity(
                id = 0L,
                name = "Tavern",
                description = "Warm lights",
                tags = "tavern,roleplay",
            )
        )
    }

    @Test
    fun observeScenesForSession_returns_linked_scenes() = runTest {
        // Arrange
        sessionSceneDao.emitLinkedScenes(
            listOf(
                SceneEntity(
                    id = 5L,
                    name = "Dungeon",
                    description = null,
                    tags = "combat",
                )
            )
        )

        // Act
        val result = repository.observeScenesForSession(sessionId = 2L).first()

        // Assert
        assertThat(result).containsExactly(
            Scene(
                id = 5L,
                name = "Dungeon",
                description = null,
                tags = listOf("combat"),
                soundscapeCount = 0,
            )
        )
    }

    @Test
    fun observeAvailableScenesForSession_returns_unlinked_scenes() = runTest {
        // Arrange
        sessionSceneDao.emitAvailableScenes(
            listOf(
                SceneEntity(
                    id = 6L,
                    name = "City",
                    description = null,
                    tags = "city",
                )
            )
        )

        // Act
        val result = repository.observeAvailableScenesForSession(sessionId = 2L).first()

        // Assert
        assertThat(result).containsExactly(
            Scene(
                id = 6L,
                name = "City",
                description = null,
                tags = listOf("city"),
                soundscapeCount = 0,
            )
        )
    }

    @Test
    fun linkScenesToSession_inserts_cross_refs_for_each_scene() = runTest {
        // Arrange
        val sessionId = 3L

        // Act
        repository.linkScenesToSession(sessionId = sessionId, sceneIds = listOf(10L, 11L))

        // Assert
        assertThat(sessionSceneDao.linkedCrossRefs).containsExactly(
            SessionSceneCrossRef(sessionId = sessionId, sceneId = 10L),
            SessionSceneCrossRef(sessionId = sessionId, sceneId = 11L),
        )
    }

    @Test
    fun unlinkSceneFromSession_deletes_only_the_link() = runTest {
        // Arrange
        val sessionId = 3L
        val sceneId = 10L

        // Act
        repository.unlinkSceneFromSession(sessionId = sessionId, sceneId = sceneId)

        // Assert
        assertThat(sessionSceneDao.unlinkedPairs).containsExactly(sessionId to sceneId)
    }

    private class FakeSceneDao : SceneDao {
        private val scenesFlow = MutableStateFlow<List<SceneEntity>>(emptyList())
        private val sceneFlows = mutableMapOf<Long, MutableStateFlow<SceneEntity?>>()

        val upsertedScenes = mutableListOf<SceneEntity>()
        val deletedSceneIds = mutableListOf<Long>()

        fun emitScenes(scenes: List<SceneEntity>) {
            scenesFlow.value = scenes
        }

        override fun observeAll(): Flow<List<SceneEntity>> = scenesFlow

        override fun observeById(sceneId: Long): Flow<SceneEntity?> =
            sceneFlows.getOrPut(sceneId) { MutableStateFlow(null) }

        override suspend fun upsert(scene: SceneEntity): Long {
            upsertedScenes += scene
            return scene.id
        }

        override suspend fun deleteById(sceneId: Long) {
            deletedSceneIds += sceneId
        }
    }

    private class FakeSessionSceneDao : SessionSceneDao {
        private val linkedScenesFlow = MutableStateFlow<List<SceneEntity>>(emptyList())
        private val availableScenesFlow = MutableStateFlow<List<SceneEntity>>(emptyList())

        val linkedCrossRefs = mutableListOf<SessionSceneCrossRef>()
        val unlinkedPairs = mutableListOf<Pair<Long, Long>>()

        fun emitLinkedScenes(scenes: List<SceneEntity>) {
            linkedScenesFlow.value = scenes
        }

        fun emitAvailableScenes(scenes: List<SceneEntity>) {
            availableScenesFlow.value = scenes
        }

        override fun observeScenesBySession(sessionId: Long): Flow<List<SceneEntity>> = linkedScenesFlow

        override fun observeAvailableScenesForSession(sessionId: Long): Flow<List<SceneEntity>> =
            availableScenesFlow

        override suspend fun link(crossRefs: List<SessionSceneCrossRef>) {
            linkedCrossRefs += crossRefs
        }

        override suspend fun unlink(sessionId: Long, sceneId: Long) {
            unlinkedPairs += sessionId to sceneId
        }
    }
}
