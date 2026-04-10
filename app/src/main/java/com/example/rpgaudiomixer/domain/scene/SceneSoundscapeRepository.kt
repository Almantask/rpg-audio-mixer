package com.example.rpgaudiomixer.domain.scene

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.flow.Flow

interface SceneSoundscapeRepository {
    fun observeSceneSoundscapes(sceneId: Long): Flow<List<SceneSoundscape>>

    fun observeAvailableSoundscapes(sceneId: Long): Flow<List<SoundscapeCategory>>

    fun observeTracks(categoryId: Long): Flow<List<SoundscapeTrack>>

    suspend fun addSoundscapeToScene(sceneId: Long, categoryId: Long)

    suspend fun removeSoundscapeFromScene(sceneId: Long, categoryId: Long)

    suspend fun updateMixVolume(sceneId: Long, categoryId: Long, mixVolume: Float)

    suspend fun updateIntensityLevel(sceneId: Long, categoryId: Long, intensityLevel: IntensityLevel)

    suspend fun reorderSoundscapes(sceneId: Long, orderedCategoryIds: List<Long>)
}
