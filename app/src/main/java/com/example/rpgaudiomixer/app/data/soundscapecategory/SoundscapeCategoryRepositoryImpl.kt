package com.example.rpgaudiomixer.app.data.soundscapecategory

import com.example.rpgaudiomixer.app.data.local.dao.SoundscapeCategoryDao
import com.example.rpgaudiomixer.app.data.local.entities.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.app.domain.repository.SoundscapeCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundscapeCategoryRepositoryImpl @Inject constructor(
    private val soundscapeCategoryDao: SoundscapeCategoryDao,
) : SoundscapeCategoryRepository {

    override fun observeByScene(sceneId: Long): Flow<List<SoundscapeCategory>> =
        soundscapeCategoryDao.observeByScene(sceneId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun addCategory(sceneId: Long, name: String) {
        soundscapeCategoryDao.upsert(SoundscapeCategoryEntity(sceneId = sceneId, name = name))
    }

    override suspend fun deleteCategory(id: Long) {
        soundscapeCategoryDao.delete(id)
    }

    override suspend fun deleteAll() {
        soundscapeCategoryDao.deleteAll()
    }

    private fun SoundscapeCategoryEntity.toDomain() = SoundscapeCategory(
        id = id,
        sceneId = sceneId,
        name = name,
        position = position,
    )
}
