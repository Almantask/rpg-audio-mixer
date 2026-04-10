package com.example.rpgaudiomixer.data.soundscape

import androidx.room.withTransaction
import com.example.rpgaudiomixer.data.local.AppDatabase
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackEntity
import com.example.rpgaudiomixer.data.soundscape.local.asDomain
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.model.SoundscapeTrackDraft
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SoundscapeRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val soundscapeCategoryDao: SoundscapeCategoryDao,
    private val soundscapeTrackDao: SoundscapeTrackDao,
) : SoundscapeRepository {

    override fun observeCategories(): Flow<List<SoundscapeCategory>> {
        return soundscapeCategoryDao.observeAll().map { categories ->
            categories.map { it.asDomain() }
        }
    }

    override suspend fun getCategory(categoryId: Long): SoundscapeCategory? {
        return soundscapeCategoryDao.getById(categoryId)?.asDomain()
    }

    override suspend fun getTracks(categoryId: Long): List<SoundscapeTrack> {
        return soundscapeTrackDao.getByCategory(categoryId).map { track -> track.asDomain() }
    }

    override suspend fun saveComposition(
        categoryId: Long?,
        name: String,
        tracks: List<SoundscapeTrackDraft>,
    ): Long {
        return appDatabase.withTransaction {
            val trimmedName = name.trim()
            val resolvedCategoryId = soundscapeCategoryDao.upsert(
                SoundscapeCategoryEntity(
                    id = categoryId ?: 0,
                    name = trimmedName,
                    iconResId = null,
                    themeLabel = trimmedName
                        .split(" ")
                        .firstOrNull()
                        ?.uppercase(),
                ),
            )

            val existingTrackIds = soundscapeTrackDao.getByCategory(resolvedCategoryId)
                .map { track -> track.id }
                .toSet()
            val incomingTrackIds = tracks.mapNotNull { track -> track.id }.toSet()
            val trackIdsToDelete = existingTrackIds - incomingTrackIds

            if (trackIdsToDelete.isNotEmpty()) {
                soundscapeTrackDao.deleteByIds(trackIdsToDelete.toList())
            }

            tracks.forEach { track ->
                soundscapeTrackDao.upsert(
                    SoundscapeTrackEntity(
                        id = track.id ?: 0,
                        categoryId = resolvedCategoryId,
                        name = track.name.trim(),
                        filePath = track.filePath,
                        intensityLevel = track.intensityLevel.value,
                        mixVolume = track.mixVolume,
                    ),
                )
            }

            resolvedCategoryId
        }
    }

    override suspend fun deleteCategory(categoryId: Long) {
        soundscapeCategoryDao.delete(categoryId)
    }
}
