package com.example.rpgaudiomixer.infra.storage.repository

import com.example.rpgaudiomixer.domain.model.FxEffect
import com.example.rpgaudiomixer.domain.storage.FxRepository
import com.example.rpgaudiomixer.infra.storage.db.dao.FxDao
import com.example.rpgaudiomixer.infra.storage.db.entity.FxEffectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomFxRepository @Inject constructor(
    private val dao: FxDao,
) : FxRepository {

    override fun getAllEffects(): Flow<List<FxEffect>> =
        dao.getAllEffects().map { it.map(FxEffectEntity::toDomain) }

    override fun getEffectById(id: Long): Flow<FxEffect?> =
        dao.getEffectById(id).map { it?.toDomain() }

    override suspend fun insert(effect: FxEffect): Long =
        dao.insert(effect.toEntity())

    override suspend fun update(effect: FxEffect) =
        dao.update(effect.toEntity())

    override suspend fun delete(effect: FxEffect) =
        dao.delete(effect.toEntity())

    override suspend fun incrementPlayCount(id: Long) =
        dao.incrementPlayCount(id)
}

private fun FxEffectEntity.toDomain() = FxEffect(
    id = id, name = name, trackFilePath = trackFilePath,
    tags = tags, durationMs = durationMs, playCount = playCount, createdAt = createdAt,
)

private fun FxEffect.toEntity() = FxEffectEntity(
    id = id, name = name, trackFilePath = trackFilePath,
    tags = tags, durationMs = durationMs, playCount = playCount, createdAt = createdAt,
)
