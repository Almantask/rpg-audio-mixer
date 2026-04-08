package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.local.SceneDao
import com.example.rpgaudiomixer.data.local.SceneEntity
import com.example.rpgaudiomixer.data.local.SessionSceneCrossRef
import com.example.rpgaudiomixer.data.local.SessionSceneDao
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SceneRepositoryImpl @Inject constructor(
    private val sceneDao: SceneDao,
    private val sessionSceneDao: SessionSceneDao,
) : SceneRepository {

    override fun observeScenes(): Flow<List<Scene>> =
        sceneDao.observeAll().map { scenes -> scenes.map(SceneEntity::toDomain) }

    override fun observeScene(sceneId: Long): Flow<Scene?> =
        sceneDao.observeById(sceneId).map { scene -> scene?.toDomain() }

    override fun observeScenesForSession(sessionId: Long): Flow<List<Scene>> =
        sessionSceneDao.observeScenesBySession(sessionId).map { scenes ->
            scenes.map(SceneEntity::toDomain)
        }

    override fun observeAvailableScenesForSession(sessionId: Long): Flow<List<Scene>> =
        sessionSceneDao.observeAvailableScenesForSession(sessionId).map { scenes ->
            scenes.map(SceneEntity::toDomain)
        }

    override suspend fun createScene(
        name: String,
        description: String?,
        tags: List<String>,
    ): Long = sceneDao.upsert(
        SceneEntity(
            name = name,
            description = description,
            tags = tags.normalizedCsv(),
        )
    )

    override suspend fun deleteScene(sceneId: Long) {
        sceneDao.deleteById(sceneId)
    }

    override suspend fun linkScenesToSession(sessionId: Long, sceneIds: List<Long>) {
        sessionSceneDao.link(
            sceneIds.distinct().map { sceneId ->
                SessionSceneCrossRef(sessionId = sessionId, sceneId = sceneId)
            }
        )
    }

    override suspend fun unlinkSceneFromSession(sessionId: Long, sceneId: Long) {
        sessionSceneDao.unlink(sessionId = sessionId, sceneId = sceneId)
    }
}

private fun SceneEntity.toDomain(): Scene = Scene(
    id = id,
    name = name,
    description = description,
    tags = tags.toTagList(),
    soundscapeCount = 0,
)

private fun List<String>.normalizedCsv(): String = asSequence()
    .map(String::trim)
    .filter(String::isNotBlank)
    .joinToString(separator = ",")

private fun String.toTagList(): List<String> = split(',')
    .map(String::trim)
    .filter(String::isNotBlank)
