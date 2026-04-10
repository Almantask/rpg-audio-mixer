package com.example.rpgaudiomixer.data.soundscape

import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategorySummaryRow
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeMostPlayedTrackRow
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeTrackDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoundscapeRepositoryImplTest {

    private val categoryDao: SoundscapeCategoryDao = mockk()
    private val trackDao: SoundscapeTrackDao = mockk()
    private val repository = SoundscapeRepositoryImpl(categoryDao, trackDao)

    @Test
    fun observeCategories_maps_summary_rows_to_domain_models() = runTest {
        // Arrange
        every { categoryDao.observeCategorySummaries() } returns flowOf(
            listOf(
                SoundscapeCategorySummaryRow(
                    id = 4L,
                    name = "Weather",
                    themeLabel = "Environment",
                    iconResId = null,
                    isDemoContent = false,
                    levelOneCount = 3,
                    levelTwoCount = 5,
                    levelThreeCount = 2,
                    totalPlayCount = 84,
                ),
            ),
        )

        // Act
        val categories = repository.observeCategories().first()

        // Assert
        assertThat(categories).containsExactly(
            com.example.rpgaudiomixer.domain.model.SoundscapeCategory(
                id = 4L,
                name = "Weather",
                themeLabel = "Environment",
                iconResId = null,
                isDemoContent = false,
                levelOneCount = 3,
                levelTwoCount = 5,
                levelThreeCount = 2,
                totalPlayCount = 84,
            ),
        )
    }

    @Test
    fun createCategory_persists_a_trimmed_name() = runTest {
        // Arrange
        coEvery { categoryDao.insert(any()) } returns 9L

        // Act
        repository.createCategory("  Arcane  ")

        // Assert
        coVerify(exactly = 1) {
            categoryDao.insert(
                match { entity ->
                    entity.id == 0L &&
                        entity.name == "Arcane" &&
                        entity.isDemoContent.not()
                },
            )
        }
    }

    @Test
    fun observeMostPlayedTrack_maps_the_row_to_a_domain_model() = runTest {
        // Arrange
        every { trackDao.observeMostPlayedTrack() } returns flowOf(
            SoundscapeMostPlayedTrackRow(
                id = 5L,
                categoryId = 4L,
                categoryName = "Interior",
                name = "Tavern Warmth",
                filePath = "content://tavern-warmth",
                intensityLevel = 2,
                mixVolumePercent = 80,
                displayOrder = 1,
                playCount = 14,
            ),
        )

        // Act
        val result = repository.observeMostPlayedTrack().first()

        // Assert
        assertThat(result).isEqualTo(
            com.example.rpgaudiomixer.domain.model.MostPlayedSoundscapeTrack(
                id = 5L,
                categoryId = 4L,
                categoryName = "Interior",
                name = "Tavern Warmth",
                filePath = "content://tavern-warmth",
                intensityLevel = com.example.rpgaudiomixer.domain.model.IntensityLevel.II,
                mixVolumePercent = 80,
                displayOrder = 1,
                playCount = 14,
            ),
        )
    }

    @Test
    fun deleteCategory_soft_deletes_the_category() = runTest {
        // Arrange
        coEvery { categoryDao.softDeleteById(4L, 800L) } returns Unit

        // Act
        repository.deleteCategory(categoryId = 4L, deletedAtMillis = 800L)

        // Assert
        coVerify(exactly = 1) { categoryDao.softDeleteById(4L, 800L) }
    }
}
