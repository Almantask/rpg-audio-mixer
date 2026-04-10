package com.example.rpgaudiomixer.data.scene

import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeDao
import com.example.rpgaudiomixer.data.scene.local.SceneSoundscapeRow
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategorySummaryRow
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SceneSoundscapeRepositoryImplTest {

    private val sceneSoundscapeDao: SceneSoundscapeDao = mockk()
    private val soundscapeCategoryDao: SoundscapeCategoryDao = mockk()
    private val soundscapeTrackDao: SoundscapeTrackDao = mockk()
    private val repository = SceneSoundscapeRepositoryImpl(
        sceneSoundscapeDao = sceneSoundscapeDao,
        soundscapeCategoryDao = soundscapeCategoryDao,
        soundscapeTrackDao = soundscapeTrackDao,
    )

    @Test
    fun observeSceneSoundscapes_maps_join_rows_to_domain_models() = runTest {
        // Arrange
        every { sceneSoundscapeDao.observeSoundscapesByScene(9L) } returns flowOf(
            listOf(
                SceneSoundscapeRow(
                    sceneId = 9L,
                    categoryId = 3L,
                    categoryName = "Weather",
                    themeLabel = "Environment",
                    iconResId = null,
                    isDemoContent = false,
                    mixVolume = 0.5f,
                    intensityLevel = IntensityLevel.II.dbValue,
                    displayOrder = 0,
                    levelOneCount = 1,
                    levelTwoCount = 2,
                    levelThreeCount = 0,
                ),
            ),
        )

        // Act
        val soundscapes = repository.observeSceneSoundscapes(9L).first()

        // Assert
        assertThat(soundscapes).containsExactly(
            com.example.rpgaudiomixer.domain.model.SceneSoundscape(
                sceneId = 9L,
                categoryId = 3L,
                categoryName = "Weather",
                themeLabel = "Environment",
                iconResId = null,
                isDemoContent = false,
                mixVolume = 0.5f,
                intensityLevel = IntensityLevel.II,
                displayOrder = 0,
                levelOneCount = 1,
                levelTwoCount = 2,
                levelThreeCount = 0,
            ),
        )
    }

    @Test
    fun addSoundscapeToScene_uses_the_next_display_order_and_default_values() = runTest {
        // Arrange
        coEvery { sceneSoundscapeDao.nextDisplayOrder(4L) } returns 2
        coEvery { sceneSoundscapeDao.insert(any()) } returns Unit

        // Act
        repository.addSoundscapeToScene(sceneId = 4L, categoryId = 8L)

        // Assert
        coVerify(exactly = 1) {
            sceneSoundscapeDao.insert(
                match { crossRef ->
                    crossRef.sceneId == 4L &&
                        crossRef.categoryId == 8L &&
                        crossRef.displayOrder == 2 &&
                        crossRef.mixVolume == 1f &&
                        crossRef.intensityLevel == IntensityLevel.I.dbValue
                },
            )
        }
    }

    @Test
    fun observeAvailableSoundscapes_filters_out_empty_and_already_added_categories() = runTest {
        // Arrange
        every { sceneSoundscapeDao.observeLinkedCategoryIds(4L) } returns flowOf(listOf(2L))
        every { soundscapeCategoryDao.observeCategorySummaries() } returns flowOf(
            listOf(
                SoundscapeCategorySummaryRow(
                    id = 1L,
                    name = "Weather",
                    themeLabel = null,
                    iconResId = null,
                    isDemoContent = false,
                    levelOneCount = 1,
                    levelTwoCount = 0,
                    levelThreeCount = 0,
                    totalPlayCount = 84,
                ),
                SoundscapeCategorySummaryRow(
                    id = 2L,
                    name = "Interior",
                    themeLabel = null,
                    iconResId = null,
                    isDemoContent = false,
                    levelOneCount = 3,
                    levelTwoCount = 0,
                    levelThreeCount = 0,
                    totalPlayCount = 142,
                ),
                SoundscapeCategorySummaryRow(
                    id = 3L,
                    name = "Empty",
                    themeLabel = null,
                    iconResId = null,
                    isDemoContent = false,
                    levelOneCount = 0,
                    levelTwoCount = 0,
                    levelThreeCount = 0,
                    totalPlayCount = 0,
                ),
            ),
        )

        // Act
        val categories = repository.observeAvailableSoundscapes(4L).first()

        // Assert
        assertThat(categories.map { it.name }).isEqualTo(listOf("Weather"))
        assertThat(categories.single().totalPlayCount).isEqualTo(84)
    }

    @Test
    fun incrementTrackPlayCount_updates_the_selected_soundscape_track() = runTest {
        // Arrange
        coEvery { soundscapeTrackDao.incrementPlayCount(9L) } returns Unit

        // Act
        repository.incrementTrackPlayCount(trackId = 9L)

        // Assert
        coVerify(exactly = 1) { soundscapeTrackDao.incrementPlayCount(9L) }
    }
}
