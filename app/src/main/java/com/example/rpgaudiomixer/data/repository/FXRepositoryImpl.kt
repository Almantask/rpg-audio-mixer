package com.example.rpgaudiomixer.data.repository

import com.example.rpgaudiomixer.data.local.FXDao
import com.example.rpgaudiomixer.data.local.FXEntity
import com.example.rpgaudiomixer.domain.model.FX
import com.example.rpgaudiomixer.domain.repository.FXRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FXRepositoryImpl @Inject constructor(
    private val dao: FXDao
) : FXRepository {
    override fun observeAll(): Flow<List<FX>> =
        dao.observeAll().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun upsert(fx: FX): Long =
        dao.upsert(fx.toEntity())

    override suspend fun delete(fx: FX) =
        dao.delete(fx.toEntity())
}

private fun FXEntity.toDomain() = FX(
    id = id,
    name = name,
    uri = uri,
    tags = tags.split(',').map { it.trim() }.filter { it.isNotEmpty() }
)

private fun FX.toEntity() = FXEntity(
    id = id,
    name = name,
    uri = uri,
    tags = tags.joinToString(",")
)
