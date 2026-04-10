package com.example.rpgaudiomixer.data.soundscape

import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategoryDao
import com.example.rpgaudiomixer.data.soundscape.local.SoundscapeCategorySummaryRow
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
}
