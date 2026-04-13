package com.example.rpgaudiomixer.app.data.soundscape

import com.example.rpgaudiomixer.app.data.local.dao.SoundscapeCategoryDao
import com.example.rpgaudiomixer.app.data.local.entities.SoundscapeCategoryEntity
import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoundscapeCategoryRepositoryImplTest {

    private val mockDao: SoundscapeCategoryDao = mockk()
    private val sut = SoundscapeCategoryRepositoryImpl(mockDao)

    // --- observeAll ---

    @Test
    fun `observeAll maps entities to domain models`() = runTest {
        // Arrange
        val entity = SoundscapeCategoryEntity(
            id = 1, name = "Ambient", sortOrder = 0,
            isDeleted = false, deletedAt = null
        )
        every { mockDao.observeAll() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(1)
        val category = result[0]
        assertThat(category.id).isEqualTo(1)
        assertThat(category.name).isEqualTo("Ambient")
        assertThat(category.sortOrder).isEqualTo(0)
        assertThat(category.isDeleted).isFalse()
        assertThat(category.deletedAt).isNull()
    }

    @Test
    fun `observeAll maps multiple entities`() = runTest {
        // Arrange
        val entities = listOf(
            SoundscapeCategoryEntity(id = 1, name = "Ambient", sortOrder = 0),
            SoundscapeCategoryEntity(id = 2, name = "Music", sortOrder = 1),
            SoundscapeCategoryEntity(id = 3, name = "Effects", sortOrder = 2)
        )
        every { mockDao.observeAll() } returns flowOf(entities)

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).hasSize(3)
        assertThat(result[0].name).isEqualTo("Ambient")
        assertThat(result[1].name).isEqualTo("Music")
        assertThat(result[2].name).isEqualTo("Effects")
    }

    @Test
    fun `observeAll returns empty list when no categories`() = runTest {
        // Arrange
        every { mockDao.observeAll() } returns flowOf(emptyList())

        // Act
        val result = sut.observeAll().first()

        // Assert
        assertThat(result).isEmpty()
    }

    // --- observeDeleted ---

    @Test
    fun `observeDeleted maps deleted entities to domain models`() = runTest {
        // Arrange
        val entity = SoundscapeCategoryEntity(
            id = 5, name = "Archived Category", sortOrder = 3,
            isDeleted = true, deletedAt = 4000L
        )
        every { mockDao.observeDeleted() } returns flowOf(listOf(entity))

        // Act
        val result = sut.observeDeleted().first()

        // Assert
        assertThat(result).hasSize(1)
        val category = result[0]
        assertThat(category.id).isEqualTo(5)
        assertThat(category.name).isEqualTo("Archived Category")
        assertThat(category.sortOrder).isEqualTo(3)
        assertThat(category.isDeleted).isTrue()
        assertThat(category.deletedAt).isEqualTo(4000L)
    }

    // --- createCategory ---

    @Test
    fun `createCategory upserts entity and returns id`() = runTest {
        // Arrange
        val entitySlot = slot<SoundscapeCategoryEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 7L

        // Act
        val result = sut.createCategory("Weather", 5)

        // Assert
        assertThat(result).isEqualTo(7L)
        coVerify { mockDao.upsert(any()) }
        assertThat(entitySlot.captured.name).isEqualTo("Weather")
        assertThat(entitySlot.captured.sortOrder).isEqualTo(5)
        assertThat(entitySlot.captured.id).isEqualTo(0)
    }

    @Test
    fun `createCategory with default sortOrder`() = runTest {
        // Arrange
        val entitySlot = slot<SoundscapeCategoryEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 8L

        // Act
        sut.createCategory("Simple")

        // Assert
        assertThat(entitySlot.captured.sortOrder).isEqualTo(0)
    }

    // --- updateCategory ---

    @Test
    fun `updateCategory upserts the mapped entity`() = runTest {
        // Arrange
        val entitySlot = slot<SoundscapeCategoryEntity>()
        coEvery { mockDao.upsert(capture(entitySlot)) } returns 10L

        // Act
        sut.updateCategory(
            SoundscapeCategory(
                id = 10, name = "Updated Category", sortOrder = 7,
                isDeleted = false, deletedAt = null
            )
        )

        // Assert
        coVerify { mockDao.upsert(any()) }
        assertThat(entitySlot.captured.id).isEqualTo(10)
        assertThat(entitySlot.captured.name).isEqualTo("Updated Category")
        assertThat(entitySlot.captured.sortOrder).isEqualTo(7)
    }

    // --- deleteCategory ---

    @Test
    fun `deleteCategory soft-deletes via dao`() = runTest {
        // Arrange
        coEvery { mockDao.softDelete(any()) } just Runs

        // Act
        sut.deleteCategory(42)

        // Assert
        coVerify { mockDao.softDelete(42) }
    }

    // --- restoreCategory ---

    @Test
    fun `restoreCategory delegates to dao restore`() = runTest {
        // Arrange
        coEvery { mockDao.restore(any()) } just Runs

        // Act
        sut.restoreCategory(7)

        // Assert
        coVerify { mockDao.restore(7) }
    }

    // --- permanentlyDeleteCategory ---

    @Test
    fun `permanentlyDeleteCategory delegates to dao permanentlyDelete`() = runTest {
        // Arrange
        coEvery { mockDao.permanentlyDelete(any()) } just Runs

        // Act
        sut.permanentlyDeleteCategory(13)

        // Assert
        coVerify { mockDao.permanentlyDelete(13) }
    }
}
