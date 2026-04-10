package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneFxCrossRef
import com.example.rpgaudiomixer.data.local.SceneFxDao
import com.example.rpgaudiomixer.data.local.SceneFxSummaryEntity
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.data.local.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.local.SceneSoundscapeSummaryEntity
import com.example.rpgaudiomixer.data.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneRepositoryImplTest {

    private val sceneDao = FakeSceneDao()
    private val sessionSceneDao = FakeSessionSceneDao()
    private val sceneSoundscapeDao = FakeSceneSoundscapeDao()
    private val sceneFxDao = FakeSceneFxDao()
    private val repository = SceneRepositoryImpl(
        sceneDao = sceneDao,
        sessionSceneDao = sessionSceneDao,
        sceneSoundscapeDao = sceneSoundscapeDao,
        sceneFxDao = sceneFxDao,
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
    fun cloneScene_duplicates_the_scene_and_its_audio_links() = runTest {
        // Arrange
        sceneDao.sceneById[7L] = SceneEntity(
            id = 7L,
            name = "Forest Night",
            description = "Owls and fog",
            tags = "forest,night",
        )
        sceneDao.nextUpsertId = 42L
        sceneSoundscapeDao.existingCrossRefs = listOf(
            SceneSoundscapeCrossRef(
                sceneId = 7L,
                categoryId = 10L,
                displayOrder = 0,
                mixVolume = 0.4f,
                intensityLevel = 2,
            )
        )
        sceneFxDao.existingCrossRefs = listOf(
            SceneFxCrossRef(
                sceneId = 7L,
                fxTrackId = 99L,
                displayOrder = 1,
            )
        )

        // Act
        val clonedSceneId = repository.cloneScene(
            sceneId = 7L,
            name = "Forest Dawn",
        )

        // Assert
        assertThat(clonedSceneId).isEqualTo(42L)
        assertThat(sceneDao.upsertedScenes).containsExactly(
            SceneEntity(
                id = 0L,
                name = "Forest Dawn",
                description = "Owls and fog",
                tags = "forest,night",
            )
        )
        assertThat(sceneSoundscapeDao.reorderedCrossRefs).containsExactly(
            SceneSoundscapeCrossRef(
                sceneId = 42L,
                categoryId = 10L,
                displayOrder = 0,
                mixVolume = 0.4f,
                intensityLevel = 2,
            )
        )
        assertThat(sceneFxDao.reorderedCrossRefs).containsExactly(
            SceneFxCrossRef(
                sceneId = 42L,
                fxTrackId = 99L,
                displayOrder = 1,
            )
        )
    }

    @Test
    fun updateScene_persists_the_updated_fields_and_normalized_tags() = runTest {
        // Arrange

        // Act
        repository.updateScene(
            sceneId = 7L,
            name = "Forest Ambush",
            description = "Dense fog",
            tags = listOf(" Forest ", "combat", ""),
        )

        // Assert
        assertThat(sceneDao.upsertedScenes).containsExactly(
            SceneEntity(
                id = 7L,
                name = "Forest Ambush",
                description = "Dense fog",
                tags = "Forest,combat",
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

    @Test
    fun observeSoundscapesForScene_maps_joined_rows_to_domain_models() = runTest {
        // Arrange
        sceneSoundscapeDao.emitSoundscapes(
            listOf(
                SceneSoundscapeSummaryEntity(
                    sceneId = 9L,
                    categoryId = 4L,
                    categoryName = "Weather",
                    iconResId = null,
                    themeLabel = "Environment",
                    levelOneTrackCount = 2,
                    levelTwoTrackCount = 1,
                    levelThreeTrackCount = 0,
                    displayOrder = 0,
                    mixVolume = 0.8f,
                    intensityLevel = 2,
                )
            )
        )

        // Act
        val result = repository.observeSoundscapesForScene(sceneId = 9L).first()

        // Assert
        assertThat(result).containsExactly(
            SceneSoundscape(
                sceneId = 9L,
                category = SoundscapeCategory(
                    id = 4L,
                    name = "Weather",
                    iconResId = null,
                    themeLabel = "Environment",
                    levelOneTrackCount = 2,
                    levelTwoTrackCount = 1,
                    levelThreeTrackCount = 0,
                ),
                displayOrder = 0,
                mixVolume = 0.8f,
                intensityLevel = IntensityLevel.II,
            )
        )
    }

    @Test
    fun addSoundscapeToScene_inserts_a_default_cross_ref() = runTest {
        // Arrange
        sceneSoundscapeDao.nextDisplayOrder = 3

        // Act
        repository.addSoundscapeToScene(sceneId = 7L, categoryId = 12L)

        // Assert
        assertThat(sceneSoundscapeDao.upsertedCrossRefs).containsExactly(
            SceneSoundscapeCrossRef(
                sceneId = 7L,
                categoryId = 12L,
                displayOrder = 3,
                mixVolume = 1f,
                intensityLevel = IntensityLevel.I.persistedValue,
            )
        )
    }

    @Test
    fun updateSoundscapeInScene_persists_mix_and_intensity_changes() = runTest {
        // Arrange

        // Act
        repository.updateSoundscapeInScene(
            sceneId = 7L,
            categoryId = 12L,
            displayOrder = 5,
            mixVolume = 0.35f,
            intensityLevel = IntensityLevel.III,
        )

        // Assert
        assertThat(sceneSoundscapeDao.upsertedCrossRefs).containsExactly(
            SceneSoundscapeCrossRef(
                sceneId = 7L,
                categoryId = 12L,
                displayOrder = 5,
                mixVolume = 0.35f,
                intensityLevel = 3,
            )
        )
    }

    @Test
    fun reorderSoundscapes_updates_display_order_in_the_requested_sequence() = runTest {
        // Arrange
        sceneSoundscapeDao.existingCrossRefs = listOf(
            SceneSoundscapeCrossRef(
                sceneId = 7L,
                categoryId = 10L,
                displayOrder = 0,
                mixVolume = 0.2f,
                intensityLevel = 3,
            ),
            SceneSoundscapeCrossRef(
                sceneId = 7L,
                categoryId = 20L,
                displayOrder = 1,
                mixVolume = 0.7f,
                intensityLevel = 2,
            ),
            SceneSoundscapeCrossRef(
                sceneId = 7L,
                categoryId = 30L,
                displayOrder = 2,
                mixVolume = 1f,
                intensityLevel = 1,
            ),
        )

        // Act
        repository.reorderSoundscapes(
            sceneId = 7L,
            orderedCategoryIds = listOf(30L, 20L, 10L),
        )

        // Assert
        assertThat(sceneSoundscapeDao.reorderedCrossRefs).containsExactly(
            SceneSoundscapeCrossRef(
                sceneId = 7L,
                categoryId = 30L,
                displayOrder = 0,
                mixVolume = 1f,
                intensityLevel = 1,
            ),
            SceneSoundscapeCrossRef(
                sceneId = 7L,
                categoryId = 20L,
                displayOrder = 1,
                mixVolume = 0.7f,
                intensityLevel = 2,
            ),
            SceneSoundscapeCrossRef(
                sceneId = 7L,
                categoryId = 10L,
                displayOrder = 2,
                mixVolume = 0.2f,
                intensityLevel = 3,
            ),
        )
    }

    @Test
    fun removeSoundscapeFromScene_deletes_only_the_requested_link() = runTest {
        // Arrange

        // Act
        repository.removeSoundscapeFromScene(sceneId = 7L, categoryId = 12L)

        // Assert
        assertThat(sceneSoundscapeDao.removedPairs).containsExactly(7L to 12L)
    }

    @Test
    fun observeFxForScene_maps_joined_rows_to_domain_models() = runTest {
        // Arrange
        sceneFxDao.emitFx(
            listOf(
                SceneFxSummaryEntity(
                    sceneId = 9L,
                    fxTrackId = 4L,
                    name = "Thunder Crack",
                    filePath = "/fx/thunder_crack.mp3",
                    tags = "storm,combat",
                    durationMs = 1200L,
                    playCount = 3,
                    isDemo = false,
                    displayOrder = 0,
                )
            )
        )

        // Act
        val result = repository.observeFxForScene(sceneId = 9L).first()

        // Assert
        assertThat(result).containsExactly(
            SceneFx(
                sceneId = 9L,
                track = FxTrack(
                    id = 4L,
                    name = "Thunder Crack",
                    filePath = "/fx/thunder_crack.mp3",
                    tags = listOf("storm", "combat"),
                    durationMs = 1200L,
                    playCount = 3,
                    isDemo = false,
                ),
                displayOrder = 0,
            )
        )
    }

    @Test
    fun addFxToScene_inserts_a_default_cross_ref() = runTest {
        // Arrange
        sceneFxDao.nextDisplayOrder = 2

        // Act
        repository.addFxToScene(sceneId = 7L, fxTrackId = 12L)

        // Assert
        assertThat(sceneFxDao.upsertedCrossRefs).containsExactly(
            SceneFxCrossRef(
                sceneId = 7L,
                fxTrackId = 12L,
                displayOrder = 2,
            )
        )
    }

    @Test
    fun reorderFx_updates_display_order_in_the_requested_sequence() = runTest {
        // Arrange

        // Act
        repository.reorderFx(
            sceneId = 7L,
            orderedFxTrackIds = listOf(30L, 20L, 10L),
        )

        // Assert
        assertThat(sceneFxDao.reorderedCrossRefs).containsExactly(
            SceneFxCrossRef(sceneId = 7L, fxTrackId = 30L, displayOrder = 0),
            SceneFxCrossRef(sceneId = 7L, fxTrackId = 20L, displayOrder = 1),
            SceneFxCrossRef(sceneId = 7L, fxTrackId = 10L, displayOrder = 2),
        )
    }

    @Test
    fun removeFxFromScene_deletes_only_the_requested_link() = runTest {
        // Arrange

        // Act
        repository.removeFxFromScene(sceneId = 7L, fxTrackId = 12L)

        // Assert
        assertThat(sceneFxDao.removedPairs).containsExactly(7L to 12L)
    }

    private class FakeSceneDao : SceneDao {
        private val scenesFlow = MutableStateFlow<List<SceneEntity>>(emptyList())
        private val sceneFlows = mutableMapOf<Long, MutableStateFlow<SceneEntity?>>()

        val sceneById = mutableMapOf<Long, SceneEntity>()
        val upsertedScenes = mutableListOf<SceneEntity>()
        val deletedSceneIds = mutableListOf<Long>()
        var nextUpsertId: Long = 0L

        fun emitScenes(scenes: List<SceneEntity>) {
            scenesFlow.value = scenes
        }

        override fun observeAll(): Flow<List<SceneEntity>> = scenesFlow

        override fun observeById(sceneId: Long): Flow<SceneEntity?> =
            sceneFlows.getOrPut(sceneId) { MutableStateFlow(null) }

        override suspend fun upsert(scene: SceneEntity): Long {
            upsertedScenes += scene
            return if (scene.id == 0L) nextUpsertId else scene.id
        }

        override suspend fun deleteById(sceneId: Long) {
            deletedSceneIds += sceneId
        }

        override suspend fun softDeleteById(sceneId: Long, deletedAt: Long) {
            deletedSceneIds += sceneId
        }

        override fun observeDeleted(): Flow<List<SceneEntity>> = MutableStateFlow(emptyList())

        override suspend fun restoreById(sceneId: Long) = Unit

        override suspend fun deleteAllDeleted() = Unit

        override suspend fun purgeDeletedBefore(cutoffTimeMillis: Long) = Unit

        override suspend fun getById(sceneId: Long): SceneEntity? = sceneById[sceneId]
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

    private class FakeSceneSoundscapeDao : SceneSoundscapeDao {
        private val soundscapesFlow = MutableStateFlow<List<SceneSoundscapeSummaryEntity>>(emptyList())

        var nextDisplayOrder: Int = 0
        var existingCrossRefs: List<SceneSoundscapeCrossRef> = emptyList()
        val upsertedCrossRefs = mutableListOf<SceneSoundscapeCrossRef>()
        val reorderedCrossRefs = mutableListOf<SceneSoundscapeCrossRef>()
        val removedPairs = mutableListOf<Pair<Long, Long>>()

        fun emitSoundscapes(soundscapes: List<SceneSoundscapeSummaryEntity>) {
            soundscapesFlow.value = soundscapes
        }

        override fun observeSoundscapesByScene(sceneId: Long): Flow<List<SceneSoundscapeSummaryEntity>> = soundscapesFlow

        override suspend fun getNextDisplayOrder(sceneId: Long): Int = nextDisplayOrder

        override suspend fun getCrossRefs(sceneId: Long): List<SceneSoundscapeCrossRef> = existingCrossRefs

        override suspend fun upsert(crossRef: SceneSoundscapeCrossRef) {
            upsertedCrossRefs += crossRef
        }

        override suspend fun updateAll(crossRefs: List<SceneSoundscapeCrossRef>) {
            reorderedCrossRefs += crossRefs
        }

        override suspend fun remove(sceneId: Long, categoryId: Long) {
            removedPairs += sceneId to categoryId
        }
    }

    private class FakeSceneFxDao : SceneFxDao {
        private val fxFlow = MutableStateFlow<List<SceneFxSummaryEntity>>(emptyList())

        var nextDisplayOrder: Int = 0
        var existingCrossRefs: List<SceneFxCrossRef> = emptyList()
        val upsertedCrossRefs = mutableListOf<SceneFxCrossRef>()
        val reorderedCrossRefs = mutableListOf<SceneFxCrossRef>()
        val removedPairs = mutableListOf<Pair<Long, Long>>()

        fun emitFx(fx: List<SceneFxSummaryEntity>) {
            fxFlow.value = fx
        }

        override fun observeFxByScene(sceneId: Long): Flow<List<SceneFxSummaryEntity>> = fxFlow

        override suspend fun getNextDisplayOrder(sceneId: Long): Int = nextDisplayOrder

        override suspend fun getCrossRefs(sceneId: Long): List<SceneFxCrossRef> = existingCrossRefs

        override suspend fun upsert(crossRef: SceneFxCrossRef) {
            upsertedCrossRefs += crossRef
        }

        override suspend fun updateAll(crossRefs: List<SceneFxCrossRef>) {
            reorderedCrossRefs += crossRefs
        }

        override suspend fun remove(sceneId: Long, fxTrackId: Long) {
            removedPairs += sceneId to fxTrackId
        }
    }
}
