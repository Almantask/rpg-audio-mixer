package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySoundscapeRepository @Inject constructor() : SoundscapeRepository {

    private val categories = MutableStateFlow<List<SoundscapeCategory>>(emptyList())
    private val tracks = MutableStateFlow<List<SoundscapeTrack>>(emptyList())

    override fun observeAllCategories(): Flow<List<SoundscapeCategory>> {
        return categories.asStateFlow().map { categoriesList ->
            categoriesList.map { category ->
                val trackCounts = calculateTrackCounts(category.id)
                category.copy(trackCountByLevel = trackCounts)
            }
        }
    }

    override suspend fun getCategoryById(id: String): SoundscapeCategory? {
        val category = categories.value.firstOrNull { it.id == id }
        return category?.let {
            val trackCounts = calculateTrackCounts(it.id)
            it.copy(trackCountByLevel = trackCounts)
        }
    }

    override suspend fun getAllCategories(): List<SoundscapeCategory> {
        return categories.value.map { category ->
            val trackCounts = calculateTrackCounts(category.id)
            category.copy(trackCountByLevel = trackCounts)
        }
    }

    override suspend fun createCategory(
        name: String,
        iconResId: Int?,
        themeLabel: String?
    ): SoundscapeCategory {
        val category = SoundscapeCategory(
            id = UUID.randomUUID().toString(),
            name = name,
            iconResId = iconResId,
            themeLabel = themeLabel
        )
        categories.value = categories.value + category
        return category
    }

    override suspend fun updateCategory(category: SoundscapeCategory) {
        categories.value = categories.value.map {
            if (it.id == category.id) category else it
        }
    }

    override suspend fun deleteCategory(id: String) {
        categories.value = categories.value.filterNot { it.id == id }
        // Also delete all tracks in this category
        tracks.value = tracks.value.filterNot { it.categoryId == id }
    }

    override fun observeTracksByCategory(categoryId: String): Flow<List<SoundscapeTrack>> {
        return tracks.asStateFlow().map { tracksList ->
            tracksList.filter { it.categoryId == categoryId }
        }
    }

    override suspend fun getTracksByCategory(categoryId: String): List<SoundscapeTrack> {
        return tracks.value.filter { it.categoryId == categoryId }
    }

    override suspend fun getTracksByCategoryAndIntensity(
        categoryId: String,
        intensityLevel: IntensityLevel
    ): List<SoundscapeTrack> {
        return tracks.value.filter {
            it.categoryId == categoryId && it.intensityLevel == intensityLevel
        }
    }

    override suspend fun createTrack(
        categoryId: String,
        name: String,
        filePath: String,
        intensityLevel: IntensityLevel,
        mixVolume: Float
    ): SoundscapeTrack {
        val track = SoundscapeTrack(
            id = UUID.randomUUID().toString(),
            categoryId = categoryId,
            name = name,
            filePath = filePath,
            intensityLevel = intensityLevel,
            mixVolume = mixVolume
        )
        tracks.value = tracks.value + track
        return track
    }

    override suspend fun updateTrack(track: SoundscapeTrack) {
        tracks.value = tracks.value.map {
            if (it.id == track.id) track else it
        }
    }

    override suspend fun deleteTrack(id: String) {
        tracks.value = tracks.value.filterNot { it.id == id }
    }

    private fun calculateTrackCounts(categoryId: String): Map<IntensityLevel, Int> {
        val categoryTracks = tracks.value.filter { it.categoryId == categoryId }
        return IntensityLevel.entries.associateWith { level ->
            categoryTracks.count { it.intensityLevel == level }
        }
    }
}
